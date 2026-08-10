package fds.radar.service.financialProduct;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fds.radar.common.ProductStatus;
import fds.radar.common.ProductType;
import fds.radar.common.RiskLevel;
import fds.radar.dto.financialProduct.ProductDetailResponseDTO;
import fds.radar.dto.financialProduct.ProductListResponseDTO;
import fds.radar.entity.financialProduct.FinancialProducts;
import fds.radar.repository.financialProduct.FinancialProductsRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FinancialProductsService {
    
    private final FinancialProductsRepository financialProductsRepository;

    // 상품 목록 조회(비로그인 사용자도 접근 가능)
    @Transactional(readOnly=true)
    public Page<ProductListResponseDTO> getProducts(ProductType productType, RiskLevel riskLevel, Pageable pageable) {
        Page<FinancialProducts> products = financialProductsRepository
                                           .search(productType, riskLevel, ProductStatus.ON_SALE, pageable);

        return products.map(this::toListDTO);
    }

    // 상품 상세 조회(비로그인 사용자도 접근 가능)
    // 판매종료 상품도 직접 링크로는 조회 가능하게 status 조건 없이 조회
    @Transactional(readOnly=true)
    public ProductDetailResponseDTO getProductDetail(Long productId) {
        FinancialProducts product = financialProductsRepository.findById(productId)
                                                               .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        return toDetailDTO(product);
    }

    private ProductListResponseDTO toListDTO(FinancialProducts p) {
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

    private ProductDetailResponseDTO toDetailDTO(FinancialProducts p) {
        return ProductDetailResponseDTO.builder()
                                       .productId(p.getProductId())
                                       .productName(p.getProductName())
                                       .institutionName(p.getInstitution().getInstitutionName())
                                       .productType(p.getProductType())
                                       .description(p.getDescription())
                                       .riskLevel(p.getRiskLevel())
                                       .principalProtection(p.isPrincipalProtection())
                                       .minAmount(p.getMinAmount())
                                       .maxAmount(p.getMaxAmount())
                                       .subscriptionPeriod(p.getSubscriptionPeriod())
                                       .recommendedPeriod(p.getRecommendedPeriod())
                                       .expectedReturnRate(p.getExpectedReturnRate())
                                       .productStatus(p.getProductStatus())
                                       .saleStartTime(p.getSaleStartDate())
                                       .saleEndDate(p.getSaleEndDate())
                                       .build();
    }
}
