package fds.radar.dto.finance;

import lombok.Getter;

@Getter
public class LiabilityRequest {
    private String loanType;
    private Long principalAmount;
    private Long remainimgAmount;
    private Double interestRate;
    private Long financialInstitutionId;
}
