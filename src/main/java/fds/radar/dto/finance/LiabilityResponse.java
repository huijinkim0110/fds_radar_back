package fds.radar.dto.finance;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LiabilityResponse {
    private Long id;
    private String loanType;
    private long remainingAmount;
    private Double dsr;
}
