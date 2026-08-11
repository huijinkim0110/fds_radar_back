package fds.radar.dto.transaction;

import fds.radar.common.BusinessCategory;
import fds.radar.common.RiskStatus;
import fds.radar.entity.transaction.Merchants;

public class MerchantResponse {
    
    private Long id;
    private String merchatName;
    private BusinessCategory businessCategory;
    private String countryCode;
    private String region;
    private boolean onlineMerchant;
    private RiskStatus riskStatus;

    public MerchantResponse() {}
    
    public MerchantResponse (Long id, String merchantName, BusinessCategory businessCategory,
        String countryCode, String region, boolean onlineMerchant, RiskStatus riskStatus
    ) {
        this.id = id;
        this.merchatName = merchantName;
        this.businessCategory = businessCategory;
        this.countryCode = countryCode;
        this.region = region;
        this.onlineMerchant = onlineMerchant;
        this.riskStatus = riskStatus;
    }

    public static MerchantResponse from(Merchants merchants) {
        return new MerchantResponse(
            merchants.getMerchantId(),
            merchants.getMerchantName(),
            merchants.getBusinessCategory(),
            merchants.getCountryCode(),
            merchants.getRegion(),
            merchants.isOnlineMerchant(),
            merchants.getRiskStatus()
        );
    }

    public Long getId() {return id;}
    public String getMerchantName() {return merchatName;}
    public BusinessCategory getBusinessCategory() {return businessCategory;}
    public String getCountryCode() {return countryCode;}
    public String getRegion() {return region;}
    public boolean isOnlineMerchant() {return onlineMerchant;}
    public RiskStatus getRiskStatus() {return riskStatus;}
}
