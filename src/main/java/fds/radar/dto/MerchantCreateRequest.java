package fds.radar.dto;

import fds.radar.common.BusinessCategory;
import fds.radar.common.RiskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class MerchantCreateRequest {
    
    @NotBlank(message = "가맹점명은 필수입니다.")
    @Size(max = 100, message = "가맹점명은 최대 100자입니다.")
    private String merchantName;

    @NotNull(message = "업종은 필수입니다.")
    private BusinessCategory businessCategory;

    @NotBlank(message = "국가코드는 필수입니다.")
    @Size(min = 2, max = 2, message = "국가코드는 ISO 2자리입니다.")
    private String countryCode;

    @NotNull(message = "지역은 필수입니다.")
    private String region;

    private boolean onlineMerchat;

    private RiskStatus riskStatus;

    public MerchantCreateRequest() {}

    public MerchantCreateRequest(String merchantName, BusinessCategory businessCategory,
        String countryCode, String region, boolean onlineMerchant, RiskStatus riskStatus
    ) {
        this.merchantName = merchantName;
        this.businessCategory = businessCategory;
        this.countryCode = countryCode;
        this.region = region;
        this.onlineMerchat = onlineMerchant;
        this.riskStatus = riskStatus;
    }

    public String getMerchantName() {return merchantName;}
    public BusinessCategory getBusinessCategory() {return businessCategory;}
    public String getCountryCode() {return countryCode;}
    public String getRegoin() {return region;}
    public boolean isOnlineMerchant() {return onlineMerchat;}
    public RiskStatus getRiskStatus() {return riskStatus;}
}
