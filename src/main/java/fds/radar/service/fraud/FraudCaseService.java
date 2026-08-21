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
import fds.radar.entity.dispute.LockRequests;
import fds.radar.entity.fraud.FraudCases;
import fds.radar.entity.fraud.FraudDetectionResults;
import fds.radar.entity.user.Users;
import fds.radar.repository.fraud.FraudCaseRepository;
import fds.radar.repository.fraud.LockRequestRepository;
import fds.radar.repository.user.UserRepository;
import fds.radar.service.fraud.vo.LockResult;
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
    private final LockRequestRepository lockRequestRepository;
    private final LockService lockService;
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

    /**
     * 4차: 탐지결과의 이상확률이 threshold 이상이면 FraudCase를 자동 생성한다.
     * threshold 미만이면 아무것도 하지 않고 Optional.empty() 반환.
     */
    public Optional<FraudCases> createCaseIfNeeded(FraudDetectionResults detectionResult) {
        if (detectionResult.getFraudProbability().compareTo(threshold) < 0) {
            return Optional.empty();
        }

        Long transactionId = detectionResult.getTransaction().getTransactionId();

        // 1차 방어: 같은 거래에 대해 이미 사건이 있으면 애초에 시도하지 않음 (빠른 실패, 대부분의 경우 여기서 걸러짐)
        Optional<FraudCases> existing = fraudCaseRepository.findByTransaction_TransactionId(transactionId);
        if (existing.isPresent()) {
            return existing;
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
            // 2차 방어: 위의 존재 체크와 save() 사이에 다른 스레드/요청이 끼어들어
            // 먼저 저장을 끝낸 경우 (진짜 동시 호출). DB unique 제약(uk_fraud_cases_transaction_id)에
            // 걸려서 여기로 떨어지며, 이땐 새로 만들지 않고 방금 저장된 기존 사건을 그대로 반환한다.
            return fraudCaseRepository.findByTransaction_TransactionId(transactionId);
        }

        String probabilityPercent = detectionResult.getFraudProbability()
                .multiply(new java.math.BigDecimal("100"))
                .setScale(0, java.math.RoundingMode.HALF_UP) + "%";
        
        fraudCaseHistoryService.record(
                saved,
                FraudActionType.HOLD,
                null,
                CaseStatus.RECEIVED,
                "AI 탐지 결과 이상거래 가능성이 높게 나타나(이상확률 " + probabilityPercent + ") 사건이 자동 생성되었습니다.",
                SYSTEM_ADMIN_ID
        );

        return Optional.of(saved);
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

    // 담당자 배정 드롭다운용: ADMIN 권한을 가진 사용자 목록 조회
    public java.util.List<AdminUserResponse> getAssignableAdmins() {
        return userRepository.findAll().stream()
                .filter(u -> u.getRole() == UserRole.ADMIN)
                .map(u -> AdminUserResponse.builder()
                        .userId(u.getUserId())
                        .name(u.getName())
                        .build())
                .toList();
    }

    // 관리자 마이페이지 대시보드: 배정받은 사건 수(진행중, CLOSED 제외) + 오늘 접수된 사건 수(전체 기준) + 상태별 처리 현황 요약
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

    // 관리자 마이페이지: 내 담당 사건 목록 (전체, 페이징 없음 — 개인 담당 건수라 많지 않을 것으로 가정)
    public java.util.List<FraudCaseListResponse> getMyCases(Long adminId) {
        return fraudCaseRepository.findByAssignedAdminId_UserId(adminId).stream()
                .map(this::toListResponse)
                .toList();
    }

    // 5차: 관리자 사건 목록 조회
    // findAll() 전체 조회는 사건이 몇만 건이면 한 번에 다 긁어와서 응답이 느려지고 메모리도 많이 먹기 때문에
    // Pageable을 받아 페이지 단위로만 조회하도록 변경. 정렬 기준(예: 최신순)은 호출부(Pageable)에서 지정.
    public Page<FraudCaseListResponse> getCaseList(Pageable pageable) {
        Page<FraudCases> cases = fraudCaseRepository.findAll(pageable);

        return cases.map(this::toListResponse); // 각각을 DTO로 변환
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

    // 5차: 관리자 사건 상세 조회 (거래정보 + AI탐지결과 + 사건정보 + 사용자확인결과 조합)
    public FraudCaseDetailResponse getCaseDetail(Long fraudCaseId) {
    FraudCases fraudCase = fraudCaseRepository.findById(fraudCaseId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사건입니다. id=" + fraudCaseId));

    return toDetailResponse(fraudCase); 
    }

    private FraudCaseDetailResponse toDetailResponse(FraudCases fraudCase) {
    FraudDetectionResults detectionResult = fraudCase.getDetectionResult();

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
            .priority(fraudCase.getPriority())
            .confirmation(fraudCase.getConfirmation())
            .fraudDecision(fraudCase.getFraudDecision())
            .assignedAdminId(fraudCase.getAssignedAdminId().getUserId())
            .openedAt(fraudCase.getOpenedAt())
            .closedAt(fraudCase.getClosedAt())
            .transactionId(fraudCase.getTransaction().getTransactionId())
            .detection(detection)
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

    // TODO(로그인 기능 붙으면 수정): 지금은 "로그인한 관리자가 누구인지" 알 방법이 없어서
    // 임시로 사건에 배정된 담당자(assignedAdminId)를 이력 행위자로 기록함.
    // 나중에 SecurityConfig에 JWT 인증 필터 붙고 SecurityContextHolder에서
    // 실제 로그인한 관리자 id를 꺼낼 수 있게 되면 아래 한 줄만 교체하면 됨:
    //   Long actingAdminId = SecurityContextHolder.getContext().getAuthentication()... (실제 로그인 관리자 id) 
    Long actingAdminId = fraudCase.getAssignedAdminId().getUserId();

    fraudCaseHistoryService.record(
            fraudCase,
            FraudActionType.INVESTIGATE,
            oldStatus,
            newStatus,
            "관리자가 사건 상태를 변경했습니다.",
            actingAdminId
        );
    }

    // 접수→조사중, 조사중→종결 순서만 허용 (역행/건너뛰기 차단) (from=oldStatus, to=newStatus)
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

        // TODO(로그인 기능 붙으면 수정): 배정 "행위자"도 원래는 로그인한 관리자여야 하지만,
        // 지금은 로그인 정보가 없어 새로 배정된 관리자 본인을 행위자로 기록.
        fraudCaseHistoryService.record(
            fraudCase,
            FraudActionType.INVESTIGATE,
            fraudCase.getCaseStatus(),
            fraudCase.getCaseStatus(),
            "담당자가 '" + previousAdmin.getName() + "'에서 '" + newAdmin.getName() + "'(으)로 변경되었습니다.",
            newAdmin.getUserId()
        );
    }

    // 6차: 사용자 본인거래 확인결과 반영
    public void updateConfirmation(Long fraudCaseId, FraudConfirmationRequest request) {
        FraudCases fraudCase = fraudCaseRepository.findById(fraudCaseId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사건입니다. id=" + fraudCaseId));
    
        UserConfirmation confirmation = request.getConfirmation();

        fraudCase.setConfirmation(confirmation);
        fraudCaseRepository.save(fraudCase);

         // 본인거래 확인은 관리자가 아니라 사건 당사자(user)가 하는 행동이라
        // 이력의 행위자도 관리자가 아닌 그 사건의 user로 기록.
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

    // 8차: 카드/계좌 잠금 요청 처리
    public void requestLock(Long fraudCaseId, FraudLockRequest request) {
        FraudCases fraudCase = fraudCaseRepository.findById(fraudCaseId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사건입니다. id=" + fraudCaseId));

        if (fraudCase.getCaseStatus() == CaseStatus.CLOSED) {
            throw new IllegalStateException("이미 종결된 사건에는 잠금을 요청할 수 없습니다.");
        }

        Users targetUser = fraudCase.getUser();
        
        // 1) 잠금 요청 기록 생성 (일단 RECEIVED 상태로)
        LockRequests lockRequest = LockRequests.builder()
                .fraudCase(fraudCase)
                .user(targetUser)
                .targetType(request.getTargetType())
                .requestReason(request.getRequestReason())
                .requestStatus(LockRequestStatus.RECEIVED)
                .requestedAt(LocalDateTime.now())
                .build();
        lockRequestRepository.save(lockRequest);

        // 2) 실제 잠금 처리 위임 (지금은 MockLockService가 항상 success=true 반환)
        LockResult result = lockService.lock(request.getTargetType(), targetUser.getUserId());

        // 3) 처리 결과에 따라 요청 상태 갱신
        lockRequest.setRequestStatus(result.isSuccess() ? LockRequestStatus.COMPLETED : LockRequestStatus.REJECTED);
        lockRequest.setProcessedAt(LocalDateTime.now());
        lockRequestRepository.save(lockRequest);

        // TODO(로그인 기능 붙으면 수정): 지금은 사건 담당 관리자를 행위자로 임시 기록
        Long actingAdminId = fraudCase.getAssignedAdminId().getUserId();

        fraudCaseHistoryService.record(
            fraudCase,
            FraudActionType.LOCK,
            fraudCase.getCaseStatus(),
            fraudCase.getCaseStatus(), // 잠금은 사건 상태(RECEIVED/INVESTIGATING/CLOSED) 자체를 안 바꿈
            targetTypeText(request.getTargetType()) + " 잠금 요청이 "
                    + (result.isSuccess() ? "정상적으로 처리되었습니다." : "처리에 실패했습니다."),
            actingAdminId
        );
    }

    // 9차: 최종 판정(정상/사기) + 사건 종결 처리
    public void finalizeDecision(Long fraudCaseId, FraudDecisionRequest request) {
        FraudCases fraudCase = fraudCaseRepository.findById(fraudCaseId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사건입니다. id=" + fraudCaseId));

        // 조사가 끝나지 않은 사건을 실수로 종결시키는 걸 방지
        if (fraudCase.getCaseStatus() != CaseStatus.INVESTIGATING) {
            throw new IllegalStateException(
                "조사중 상태의 사건만 최종 판정할 수 있습니다. 현재 상태: " + fraudCase.getCaseStatus());
        }

        FraudDecision decision = request.getDecision();
        TransactionStatus targetStatus = (decision == FraudDecision.FRAUD) 
                ? TransactionStatus.CANCELED 
                : TransactionStatus.APPROVED;

        // 거래 상태 변경 위임 (지금은 MockTransactionStatusService가 실제로 DB 반영까지 함)
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