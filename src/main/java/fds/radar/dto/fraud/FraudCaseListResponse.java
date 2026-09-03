package fds.radar.dto.fraud;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import fds.radar.common.CasePriority;
import fds.radar.common.CaseStatus;
import fds.radar.common.TransactionType;   // 추가
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
    private TransactionType transactionType; // 추가: 계좌이체/카드결제 구분 표시용
    private BigDecimal fraudProbability;
    private CasePriority priority;
    private CaseStatus caseStatus;
    private Long assignedAdminId;
    private LocalDateTime openedAt;
}