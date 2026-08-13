package fds.radar.dto.transaction;

import fds.radar.common.RiskStatus;
import jakarta.validation.constraints.NotNull;

public class MerchantRiskUpdateRequest {
    
    @NotNull(message = "위험상태는 필수입니다.")
    private RiskStatus riskStatus;

    public MerchantRiskUpdateRequest() {}

    public MerchantRiskUpdateRequest(RiskStatus riskStatus) {
        this.riskStatus = riskStatus;
    }

    public RiskStatus getRiskStatus() {return riskStatus;}
}
