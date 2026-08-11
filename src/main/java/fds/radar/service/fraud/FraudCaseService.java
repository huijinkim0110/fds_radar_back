package fds.radar.service.fraud;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import fds.radar.common.CasePriority;
import fds.radar.common.CaseStatus;
import fds.radar.common.FraudActionType;
import fds.radar.common.UserConfirmation;
import fds.radar.dto.fraud.FraudCaseAssignRequest;
import fds.radar.dto.fraud.FraudCaseDetailResponse;
import fds.radar.dto.fraud.FraudCaseListResponse;
import fds.radar.dto.fraud.FraudCaseStatusRequest;
import fds.radar.dto.fraud.FraudConfirmationRequest;
import fds.radar.dto.fraud.FraudDecisionRequest;
import fds.radar.entity.fraud.FraudCases;
import fds.radar.entity.fraud.FraudDetectionResults;
import fds.radar.entity.user.Users;
import fds.radar.repository.fraud.FraudCaseRepository;
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

        CasePriority priority = calculatePriority(detectionResult.getFraudProbability());

        FraudCases fraudCase = FraudCases.builder()
                .user(detectionResult.getTransaction().getUsers())
                .transaction(detectionResult.getTransaction())
                .detectionResult(detectionResult)
                .caseStatus(CaseStatus.RECEIVED)
                .priority(priority)
                .confirmation(UserConfirmation.NO_RESPONSE)
                .assignedAdminId(Users.builder().userId(SYSTEM_ADMIN_ID).build())
                .openedAt(LocalDateTime.now())
                .build();

        FraudCases saved = fraudCaseRepository.save(fraudCase);

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
    public List<FraudCaseListResponse> getCaseList() {
        throw new UnsupportedOperationException("5차에서 구현 예정");
    }

    // 5차: 관리자 사건 상세 조회 (거래정보 + AI탐지결과 + 사건정보 + 사용자확인결과 조합)
    public FraudCaseDetailResponse getCaseDetail(Long fraudCaseId) {
        throw new UnsupportedOperationException("5차에서 구현 예정");
    }

    // 6차: 사건 상태 변경 (RECEIVED → INVESTIGATING → CLOSED)
    public void updateCaseStatus(Long fraudCaseId, FraudCaseStatusRequest request) {
        throw new UnsupportedOperationException("6차에서 구현 예정");
    }

    // 6차: 담당 관리자 배정
    public void assignAdmin(Long fraudCaseId, FraudCaseAssignRequest request) {
        throw new UnsupportedOperationException("6차에서 구현 예정");
    }

    // 6차: 사용자 본인거래 확인결과 반영
    public void updateConfirmation(Long fraudCaseId, FraudConfirmationRequest request) {
        throw new UnsupportedOperationException("6차에서 구현 예정");
    }

    // 9차: 최종 판정(정상/사기) + 사건 종결 처리
    public void finalizeDecision(Long fraudCaseId, FraudDecisionRequest request) {
        throw new UnsupportedOperationException("9차에서 구현 예정");
    }
}