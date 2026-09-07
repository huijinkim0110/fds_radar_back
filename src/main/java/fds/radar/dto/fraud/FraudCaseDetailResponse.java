package fds.radar.dto.fraud;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import fds.radar.common.CasePriority;
import fds.radar.common.CaseStatus;
import fds.radar.common.FraudDecision;
import fds.radar.common.PredictedFraudType;
import fds.radar.common.PredictedResult;
import fds.radar.common.UserConfirmation;
import fds.radar.common.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FraudCaseDetailResponse {
    private Long fraudCaseId;
    private CaseStatus caseStatus;
    private CasePriority priority;
    private String origin; // "AI_DETECTION" 또는 "USER_REPORT" — 사건 생성 경로
    private UserConfirmation confirmation;
    private FraudDecision fraudDecision;
    private Long assignedAdminId;
    private LocalDateTime openedAt;
    private LocalDateTime closedAt;

    // TODO: Transactions 필드가 더 필요해지면 이 안에 확장
    private Long transactionId;
    private TransactionType transactionType;   // 신규: 잠금 대상 자동 판단용

    private DetectionSummary detection; // 탐지 관련은 여기 안에 묶음 (중첩 클래스)

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DetectionSummary {
        private Long detectionResultId;
        private BigDecimal fraudProbability;
        private PredictedResult predictedResult;
        private PredictedFraudType fraudType;
        private String detectionReason;
    }
}