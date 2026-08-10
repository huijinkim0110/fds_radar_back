package fds.radar.dto.financialProduct;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import fds.radar.common.ProductStatus;
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
public class ProductDetailResponseDTO {
    // 목록 상세용
    private Long productId;
    private String productName;
    private String institutionName;
    private ProductType productType;
    private String description;
    private RiskLevel riskLevel;
    private boolean principalProtection;
    private Long minAmount;
    private Long maxAmount;
    private Integer subscriptionPeriod;
    private Integer recommendedPeriod;
    private BigDecimal expectedReturnRate;
    private ProductStatus productStatus;
    private LocalDateTime saleStartTime;
    private LocalDateTime saleEndDate;
}
