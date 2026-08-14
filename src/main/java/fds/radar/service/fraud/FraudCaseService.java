package fds.radar.service.fraud;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import fds.radar.common.CasePriority;
import fds.radar.common.CaseStatus;
import fds.radar.common.FraudActionType;
import fds.radar.common.LockRequestStatus;
import fds.radar.common.UserConfirmation;
import fds.radar.common.UserRole;
import fds.radar.dto.fraud.FraudCaseAssignRequest;
import fds.radar.dto.fraud.FraudCaseDetailResponse;
import fds.radar.dto.fraud.FraudCaseListResponse;
import fds.radar.dto.fraud.FraudCaseStatusRequest;
import fds.radar.dto.fraud.FraudConfirmationRequest;
import fds.radar.dto.fraud.FraudDecisionRequest;
import fds.radar.dto.fraud.FraudLockRequest;
import fds.radar.entity.dispute.LockRequests;
import fds.radar.entity.fraud.FraudCases;
import fds.radar.entity.fraud.FraudDetectionResults;
import fds.radar.entity.user.Users;
import fds.radar.repository.fraud.FraudCaseRepository;
import fds.radar.repository.fraud.LockRequestRepository;
import fds.radar.repository.user.UserRepository;
import fds.radar.service.fraud.vo.LockResult;
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

        fraudCaseHistoryService.record(
                saved,
                FraudActionType.HOLD,
                null,
                CaseStatus.RECEIVED,
                "AI 탐지 결과 threshold(" + threshold + ") 초과로 사건 자동 생성. probability=" + detectionResult.getFraudProbability(),
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
            "사건 상태 변경: " + oldStatus + " → " + newStatus,
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
            "담당자 변경: " + previousAdmin.getUserId() + " → " + newAdmin.getUserId(),
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
            "사용자 본인거래 확인결과 변경: " + confirmation,
            actingUserId
        );
    }

    // 8차: 카드/계좌 잠금 요청 처리
    public void requestLock(Long fraudCaseId, FraudLockRequest request) {
        FraudCases fraudCase = fraudCaseRepository.findById(fraudCaseId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사건입니다. id=" + fraudCaseId));

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
            (result.isSuccess() ? "잠금 처리 성공: " : "잠금 처리 실패: ")
                    + request.getTargetType() + " - " + result.getMessage(),
            actingAdminId
        );
    }

    // 9차: 최종 판정(정상/사기) + 사건 종결 처리
    public void finalizeDecision(Long fraudCaseId, FraudDecisionRequest request) {
        throw new UnsupportedOperationException("9차에서 구현 예정");
    }
}