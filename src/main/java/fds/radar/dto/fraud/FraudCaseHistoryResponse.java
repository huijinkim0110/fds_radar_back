package fds.radar.dto.fraud;

import java.time.LocalDateTime;

import fds.radar.common.CaseStatus;
import fds.radar.common.FraudActionType;
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
public class FraudCaseHistoryResponse {
    private Long caseHistoryId;
    private Long fraudCaseId;
    private FraudActionType actionType;
    private CaseStatus previousStatus;
    private CaseStatus changedStatus;
    private String actionContent;
    private Long adminId;
    private LocalDateTime createdAt;
}
