package fds.radar.dto.fraud;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import fds.radar.common.CasePriority;
import fds.radar.common.CaseStatus;
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
}