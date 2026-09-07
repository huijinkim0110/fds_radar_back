package fds.radar.dto.fraud;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import fds.radar.common.CasePriority;
import fds.radar.common.CaseStatus;
import fds.radar.common.UserConfirmation;
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
public class FraudCaseListResponse {
    private Long fraudCaseId;
    private Long transactionId;
    private BigDecimal fraudProbability;
    private CasePriority priority;
    private CaseStatus caseStatus;
    private Long assignedAdminId;
    private LocalDateTime openedAt;

    // 유저 화면(거래처/금액/유형/본인확인여부) 표시용으로 추가
    private String merchantName;
    private BigDecimal amount;
    private String transactionType;
    private UserConfirmation confirmation;
    // openedAt(사건 접수시각)과 다름 — 실제 거래가 발생한 시각
    private LocalDateTime transactionOccurredAt;
}