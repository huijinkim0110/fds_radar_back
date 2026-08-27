package fds.radar.service.fraud;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import fds.radar.common.TransactionStatus;

import fds.radar.common.CasePriority;
import fds.radar.common.CaseStatus;
import fds.radar.common.FraudActionType;
import fds.radar.common.FraudDecision;
import fds.radar.common.LockRequestStatus;
import fds.radar.common.RequestTargetType;
import fds.radar.common.UserConfirmation;
import fds.radar.common.UserRole;
import fds.radar.dto.fraud.FraudCaseAssignRequest;
import fds.radar.dto.fraud.FraudCaseDetailResponse;
import fds.radar.dto.fraud.FraudCaseListResponse;
import fds.radar.dto.fraud.FraudCaseStatusRequest;
import fds.radar.dto.fraud.FraudConfirmationRequest;
import fds.radar.dto.fraud.FraudDecisionRequest;
import fds.radar.dto.fraud.FraudLockRequest;
import fds.radar.dto.fraud.AdminUserResponse;
import fds.radar.dto.fraud.AdminDashboardResponse;
import fds.radar.dto.dispute.LockRequestCreateRequest;
import fds.radar.dto.dispute.LockRequestProcessRequest;
import fds.radar.dto.dispute.LockRequestResponse;
import fds.radar.entity.fraud.FraudCases;
import fds.radar.entity.fraud.FraudDetectionResults;
import fds.radar.entity.user.Users;
import fds.radar.repository.fraud.FraudCaseRepository;
import fds.radar.repository.user.UserRepository;
import fds.radar.service.dispute.LockRequestService;
import fds.radar.service.fraud.vo.TransactionStatusResult;
import lombok.RequiredArgsConstructor;

/**
 * 이상거래 사건(FraudCase)의 조회, 상태변경, 담당자배정, 최종판정을 담당하는 서비스.
 * FraudDetectionService와 마찬가지로 구현체 교체 계획이 없어서 인터페이스 없이 클래스로 작성.
 */
@Service
@RequiredArgsConstructor
public class FraudCaseService {

    private final FraudCaseRepository fraudCaseRepository;
    private final FraudCaseHistoryService fraudCaseHistoryService;
    private final UserRepository userRepository;
    private final LockRequestService lockRequestService;
    private final TransactionStatusService transactionStatusService;

    // TODO: FraudCases.assignedAdminId, FraudCaseHistories.adminId가 둘 다 nullable=false라
    // 자동생성 시점엔 실제 담당자가 없으므로 임시로 SYSTEM 계정(userId=1)을 사용.
    // 5~6차에서 실제 담당자 배정 기능이 붙으면 이 상수는 제거 검토 필요.
    private static final Long SYSTEM_ADMIN_ID = 1L;

    @Value("${fraud.threshold}")
    private java.math.BigDecimal threshold;

    @Value("${fraud.priority.medium-min}")
    private java.math.BigDecimal mediumMin;

    @Value("${fraud.priority.high-min}")
    private java.math.BigDecimal highMin;

    // 기존 createCaseIfNeeded()를 아래처럼 교체하고, getOrCreateCase/buildAutoDetectMessage 두 개를 새로 추가

    public Optional<FraudCases> createCaseIfNeeded(FraudDetectionResults detectionResult) {
        if (detectionResult.getFraudProbability().compareTo(threshold) < 0) {
            return Optional.empty();
        }
        return Optional.of(getOrCreateCase(detectionResult, buildAutoDetectMessage(detectionResult)));
    }

    /**
     * 신고(FraudReport) 접수 시, threshold 미만이라 자동 생성되지 않았던 거래(=AI가 정상 판단한 거래)에
     * 대해 사건을 생성한다. createCaseIfNeeded()와 달리 확률 조건 없이 무조건 생성(또는 기존 것 반환)한다.
     * FraudReportService에서, 신고 대상 거래에 연결된 FraudCase가 없을 때 호출하는 용도.
     */
    public FraudCases createCaseFromReport(FraudDetectionResults detectionResult) {
        return getOrCreateCase(detectionResult, "사용자 신고(사후 신고)로 인해 사건이 생성되었습니다.");
    }

    private FraudCases getOrCreateCase(FraudDetectionResults detectionResult, String historyMessage) {
        Long transactionId = detectionResult.getTransaction().getTransactionId();

        Optional<FraudCases> existing = fraudCaseRepository.findByTransaction_TransactionId(transactionId);
        if (existing.isPresent()) {
            return existing.get();
        }

        CasePriority priority = calculatePriority(detectionResult.getFraudProbability());

        FraudCases fraudCase = FraudCases.builder()
                .user(detectionResult.getTransaction().getUser())
                .transaction(detectionResult.getTransaction())
                .detectionResult(detectionResult)
                .caseStatus(CaseStatus.RECEIVED)
                .priority(priority)
                .confirmation(UserConfirmation.NO_RESPONSE)
                .assignedAdminId(Users.builder().userId(SYSTEM_ADMIN_ID).build())
                .openedAt(LocalDateTime.now())
                .build();

        FraudCases saved;
        try {
            saved = fraudCaseRepository.save(fraudCase);
        } catch (DataIntegrityViolationException e) {
            return fraudCaseRepository.findByTransaction_TransactionId(transactionId)
                    .orElseThrow(() -> e);
        }

        fraudCaseHistoryService.record(
                saved, FraudActionType.HOLD, null, CaseStatus.RECEIVED,
                historyMessage, SYSTEM_ADMIN_ID
        );

        return saved;
    }

    private String buildAutoDetectMessage(FraudDetectionResults detectionResult) {
        String probabilityPercent = detectionResult.getFraudProbability()
                .multiply(new java.math.BigDecimal("100"))
                .setScale(0, java.math.RoundingMode.HALF_UP) + "%";
        return "AI 탐지 결과 이상거래 가능성이 높게 나타나(이상확률 " + probabilityPercent + ") 사건이 자동 생성되었습니다.";
    }

    private CasePriority calculatePriority(java.math.BigDecimal probability) {
        if (probability.compareTo(highMin) >= 0) {
            return CasePriority.HIGH;
        } else if (probability.compareTo(mediumMin) >= 0) {
            return CasePriority.MEDIUM;
        } else {
            return CasePriority.LOW;
        }
    }

    public java.util.List<AdminUserResponse> getAssignableAdmins() {
        return userRepository.findAll().stream()
                .filter(u -> u.getRole() == UserRole.ADMIN)
                .map(u -> AdminUserResponse.builder()
                        .userId(u.getUserId())
                        .name(u.getName())
                        .build())
                .toList();
    }

    public AdminDashboardResponse getDashboard(Long adminId) {
        long assignedCaseCount = fraudCaseRepository
                .countByAssignedAdminId_UserIdAndCaseStatusNot(adminId, CaseStatus.CLOSED);

        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        long todayReceivedCaseCount = fraudCaseRepository.countByOpenedAtGreaterThanEqual(todayStart);

        long receivedCaseCount = fraudCaseRepository
                .countByAssignedAdminId_UserIdAndCaseStatus(adminId, CaseStatus.RECEIVED);
        long investigatingCaseCount = fraudCaseRepository
                .countByAssignedAdminId_UserIdAndCaseStatus(adminId, CaseStatus.INVESTIGATING);
        long closedCaseCount = fraudCaseRepository
                .countByAssignedAdminId_UserIdAndCaseStatus(adminId, CaseStatus.CLOSED);

        return AdminDashboardResponse.builder()
                .assignedCaseCount(assignedCaseCount)
                .todayReceivedCaseCount(todayReceivedCaseCount)
                .receivedCaseCount(receivedCaseCount)
                .investigatingCaseCount(investigatingCaseCount)
                .closedCaseCount(closedCaseCount)
                .build();
    }

    public java.util.List<FraudCaseListResponse> getMyCases(Long adminId) {
        return fraudCaseRepository.findByAssignedAdminId_UserId(adminId).stream()
                .map(this::toListResponse)
                .toList();
    }

    public Page<FraudCaseListResponse> getCaseList(Pageable pageable) {
        Page<FraudCases> cases = fraudCaseRepository.findAll(pageable);

        return cases.map(this::toListResponse);
    }
    
    private FraudCaseListResponse toListResponse(FraudCases fraudCase) {
        return FraudCaseListResponse.builder()
            .fraudCaseId(fraudCase.getFraudCaseId())
            .transactionId(fraudCase.getTransaction().getTransactionId())
            .fraudProbability(fraudCase.getDetectionResult().getFraudProbability())
            .priority(fraudCase.getPriority())
            .caseStatus(fraudCase.getCaseStatus())
            .assignedAdminId(fraudCase.getAssignedAdminId().getUserId())
            .openedAt(fraudCase.getOpenedAt())
            .build();
    }

    public FraudCaseDetailResponse getCaseDetail(Long fraudCaseId) {
    FraudCases fraudCase = fraudCaseRepository.findById(fraudCaseId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사건입니다. id=" + fraudCaseId));

    return toDetailResponse(fraudCase); 
    }

    private FraudCaseDetailResponse toDetailResponse(FraudCases fraudCase) {
    FraudDetectionResults detectionResult = fraudCase.getDetectionResult();

    // threshold 미만인데 사건이 존재하면 신고로 생성된 것
    String origin = detectionResult.getFraudProbability().compareTo(threshold) < 0
        ? "USER_REPORT"
        : "AI_DETECTION";

    FraudCaseDetailResponse.DetectionSummary detection = FraudCaseDetailResponse.DetectionSummary.builder()
            .detectionResultId(detectionResult.getDetectionResultId())
            .fraudProbability(detectionResult.getFraudProbability())
            .predictedResult(detectionResult.getPredictedResult())
            .fraudType(detectionResult.getFraudType())
            .detectionReason(detectionResult.getDetectionReason())
            .build();

    return FraudCaseDetailResponse.builder()
            .fraudCaseId(fraudCase.getFraudCaseId())
            .caseStatus(fraudCase.getCaseStatus())
            .confirmation(fraudCase.getConfirmation())
            .fraudDecision(fraudCase.getFraudDecision())
            .assignedAdminId(fraudCase.getAssignedAdminId().getUserId())
            .openedAt(fraudCase.getOpenedAt())
            .closedAt(fraudCase.getClosedAt())
            .transactionId(fraudCase.getTransaction().getTransactionId())
            .transactionType(fraudCase.getTransaction().getTransactionType())   // 이 줄 추가
            .detection(detection)
            .priority(fraudCase.getPriority())
            .origin(origin)
            .build();
    }

    // 6차: 사건 상태 변경 (RECEIVED → INVESTIGATING → CLOSED)
    public void updateCaseStatus(Long fraudCaseId, FraudCaseStatusRequest request) {
        FraudCases fraudCase = fraudCaseRepository.findById(fraudCaseId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사건입니다. id=" + fraudCaseId));
    
    CaseStatus oldStatus = fraudCase.getCaseStatus();
    CaseStatus newStatus = request.getCaseStatus(); 
    
    validateStatusTransition(oldStatus, newStatus);

    fraudCase.setCaseStatus(newStatus);
    fraudCaseRepository.save(fraudCase);

    // 로그인 기능 반영: 프론트가 actingAdminId를 보내면 그걸 우선 사용.
    // 아직 반영 안 된 화면 대비, 안 보내면 기존 방식(배정된 담당자)으로 fallback.
    Long actingAdminId = request.getActingAdminId() != null
            ? request.getActingAdminId()
            : fraudCase.getAssignedAdminId().getUserId();

    fraudCaseHistoryService.record(
            fraudCase,
            FraudActionType.INVESTIGATE,
            oldStatus,
            newStatus,
            "관리자가 사건 상태를 변경했습니다.",
            actingAdminId
        );
    }

    private void validateStatusTransition(CaseStatus from, CaseStatus to) {
        if (from == to) {
            throw new IllegalStateException("이미 " + from + "상태입니다.");
        }
        boolean valid = (from == CaseStatus.RECEIVED && to == CaseStatus.INVESTIGATING) ||
                        (from == CaseStatus.INVESTIGATING && to == CaseStatus.CLOSED);
        if (!valid) {
            throw new IllegalStateException("허용되지 않는 상태 변경입니다. " + from + " → " + to);
        }
    }

    // 6차: 담당 관리자 배정
    public void assignAdmin(Long fraudCaseId, FraudCaseAssignRequest request) {
        FraudCases fraudCase = fraudCaseRepository.findById(fraudCaseId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사건입니다. id=" + fraudCaseId));

        if (fraudCase.getCaseStatus() == CaseStatus.CLOSED) {
            throw new IllegalStateException("이미 종결된 사건은 담당자를 재배정할 수 없습니다.");
        }
            
        Users newAdmin = userRepository.findById(request.getAdminId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 관리자입니다. id=" + request.getAdminId()));

        if (newAdmin.getRole() != UserRole.ADMIN) {
            throw new IllegalStateException("ADMIN 권한이 없는 사용자는 담당자로 배정할 수 없습니다. id=" + request.getAdminId());
        }

        Users previousAdmin = fraudCase.getAssignedAdminId();

        fraudCase.setAssignedAdminId(newAdmin);
        fraudCaseRepository.save(fraudCase);

        // 배정을 실행한(로그인한) 관리자 우선, 없으면 기존처럼 새로 배정된 관리자 본인으로 fallback
        Long actingAdminId = request.getActingAdminId() != null
                ? request.getActingAdminId()
                : newAdmin.getUserId();

        fraudCaseHistoryService.record(
            fraudCase,
            FraudActionType.INVESTIGATE,
            fraudCase.getCaseStatus(),
            fraudCase.getCaseStatus(),
            "담당자가 '" + previousAdmin.getName() + "'에서 '" + newAdmin.getName() + "'(으)로 변경되었습니다.",
            actingAdminId
        );
    }

    // 6차: 사용자 본인거래 확인결과 반영
    public void updateConfirmation(Long fraudCaseId, FraudConfirmationRequest request) {
        FraudCases fraudCase = fraudCaseRepository.findById(fraudCaseId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사건입니다. id=" + fraudCaseId));
    
        UserConfirmation confirmation = request.getConfirmation();

        fraudCase.setConfirmation(confirmation);
        fraudCaseRepository.save(fraudCase);

        Long actingUserId = fraudCase.getUser().getUserId();

        fraudCaseHistoryService.record(
            fraudCase,
            FraudActionType.CONFIRMED,
            fraudCase.getCaseStatus(),
            fraudCase.getCaseStatus(),
            "사용자가 본인거래 확인 결과를 '" + confirmation.getConfirmationResult() + "'(으)로 응답했습니다.",
            actingUserId
        );
    }

    // 8차: 카드/계좌 잠금 요청 처리 — 실제 LockRequestService 연동 (C 구현)
    public void requestLock(Long fraudCaseId, FraudLockRequest request) {
        FraudCases fraudCase = fraudCaseRepository.findById(fraudCaseId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사건입니다. id=" + fraudCaseId));

        if (fraudCase.getCaseStatus() == CaseStatus.CLOSED) {
            throw new IllegalStateException("이미 종결된 사건에는 잠금을 요청할 수 없습니다.");
        }

        // 1) 잠금 요청 생성(RECEIVED) — LockRequestService의 fraud_case 기반 생성 로직을 그대로 재사용.
        //    LockRequests 엔티티 생성/저장은 LockRequestService 내부에서만 하고, 여기서는 중복으로 만들지 않는다.
        LockRequestCreateRequest createRequest = LockRequestCreateRequest.builder()
                .farudCaseId(fraudCaseId)
                .targetType(request.getTargetType())
                .requestReason(request.getRequestReason())
                .build();
        LockRequestResponse createdLock = lockRequestService.createFromFraudCase(createRequest);

        // 2) 관리자가 사건에서 즉시 잠금을 요청한 것이므로, 생성과 동시에 승인(COMPLETED) 처리까지 한 번에 진행.
        //    LockRequestService.process()가 내부적으로 실제 카드/계좌 상태를 변경(applyLock)한다.
        //    대상(카드/계좌)을 찾지 못하는 등 처리 중 실패하면 요청을 REJECTED로 마감한다.
        boolean success;
        String failureMessage = null;
        try {
            lockRequestService.process(createdLock.getId(),
                    LockRequestProcessRequest.builder()
                            .requestStatus(LockRequestStatus.COMPLETED)
                            .build());
            success = true;
        } catch (RuntimeException e) {
            lockRequestService.process(createdLock.getId(),
                    LockRequestProcessRequest.builder()
                            .requestStatus(LockRequestStatus.REJECTED)
                            .build());
            success = false;
            failureMessage = e.getMessage();
        }

        // 잠금을 요청한(로그인한) 관리자 우선, 없으면 기존처럼 사건 담당 관리자로 fallback
        Long actingAdminId = request.getActingAdminId() != null
                ? request.getActingAdminId()
                : fraudCase.getAssignedAdminId().getUserId();

        fraudCaseHistoryService.record(
            fraudCase,
            FraudActionType.LOCK,
            fraudCase.getCaseStatus(),
            fraudCase.getCaseStatus(),
            targetTypeText(request.getTargetType()) + " 잠금 요청이 "
                    + (success ? "정상적으로 처리되었습니다." : "처리에 실패했습니다: " + failureMessage),
            actingAdminId
        );
    }

    // 9차: 최종 판정(정상/사기) + 사건 종결 처리
    public void finalizeDecision(Long fraudCaseId, FraudDecisionRequest request) {
        FraudCases fraudCase = fraudCaseRepository.findById(fraudCaseId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사건입니다. id=" + fraudCaseId));

        if (fraudCase.getCaseStatus() != CaseStatus.INVESTIGATING) {
            throw new IllegalStateException(
                "조사중 상태의 사건만 최종 판정할 수 있습니다. 현재 상태: " + fraudCase.getCaseStatus());
        }

        FraudDecision decision = request.getDecision();
        TransactionStatus targetStatus = (decision == FraudDecision.FRAUD) 
                ? TransactionStatus.CANCELED 
                : TransactionStatus.APPROVED;

        TransactionStatusResult result = transactionStatusService.updateStatus(
                fraudCase.getTransaction().getTransactionId(), targetStatus);
            
        fraudCase.setFraudDecision(decision);
        fraudCase.setCaseStatus(CaseStatus.CLOSED);
        fraudCase.setClosedAt(LocalDateTime.now());
        fraudCaseRepository.save(fraudCase);

        Long actingAdminId = fraudCase.getAssignedAdminId().getUserId();

        String decisionText = (decision == FraudDecision.FRAUD) ? "사기" : "정상";

        fraudCaseHistoryService.record(
            fraudCase,
            FraudActionType.FINALIZE,
            CaseStatus.INVESTIGATING,
            CaseStatus.CLOSED,
            "관리자가 최종 판정을 '" + decisionText + "'(으)로 확정하여, 거래 상태가 '" + targetStatus.getDescription() + "'(으)로 "
                    + (result.isSuccess() ? "반영되었습니다." : "반영되지 않았습니다: " + result.getMessage()),
            actingAdminId
    );
    }

    private String targetTypeText(RequestTargetType targetType) {
        if (targetType == null) return "-";
        switch (targetType) {
            case CARD: return "카드";
            case ACCOUNT: return "계좌";
            default: return targetType.toString();
        }
    }
}