package fds.radar.dto.financialProduct;

import java.math.BigDecimal;

import fds.radar.common.ProductType;
import fds.radar.common.RiskLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductCompareItemDTO {
    private Long comparisonItemId;
    private Long productId;
    private String productName;
    private String institutionName;
    private ProductType productType;
    private RiskLevel riskLevel;
    private BigDecimal expectedReturnRate;
    private Integer subscriptionPeriod;
    private boolean principalProtection;
}
