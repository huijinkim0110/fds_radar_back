package fds.radar.mapper;

import fds.radar.dto.financialProduct.ProductListResponseDTO;
import fds.radar.entity.financialProduct.FinancialProducts;

public class FinancialProductMapper {
    
    private FinancialProductMapper() {}

    public static ProductListResponseDTO toListDTO(FinancialProducts p) {
        return ProductListResponseDTO.builder()
                                     .productId(p.getProductId())
                                     .productName(p.getProductName())
                                     .institutionName(p.getInstitution().getInstitutionName())
                                     .productType(p.getProductType())
                                     .riskLevel(p.getRiskLevel())
                                     .expectedReturnRate(p.getExpectedReturnRate())
                                     .principalProtection(p.isPrincipalProtection())
                                     .build();
    }
}
