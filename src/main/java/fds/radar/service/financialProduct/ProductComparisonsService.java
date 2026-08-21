package fds.radar.service.financialProduct;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fds.radar.dto.financialProduct.ComparisonDetailResponseDTO;
import fds.radar.dto.financialProduct.ProductCompareItemDTO;
import fds.radar.entity.financialProduct.FinancialProducts;
import fds.radar.entity.financialProduct.ProductComparisonItems;
import fds.radar.entity.financialProduct.ProductComparisons;
import fds.radar.entity.user.Users;
import fds.radar.repository.financialProduct.FinancialProductsRepository;
import fds.radar.repository.financialProduct.ProductComparisonItemsRepository;
import fds.radar.repository.financialProduct.ProductComparisonsRepository;
import fds.radar.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductComparisonsService {
    
    private final ProductComparisonsRepository productComparisonsRepository;
    private final ProductComparisonItemsRepository productComparisonItemsRepository;
    private final FinancialProductsRepository financialProductsRepository;
    private final UserRepository userRepository;

    // 비교함 새로 생성
    @Transactional
    public ProductComparisons createComparison(Long userId, String comparisonName) {
        Users user = userRepository.findById(userId)
                                   .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        ProductComparisons comparison = ProductComparisons.builder()
                                                          .user(user)
                                                          .comparisonName(comparisonName)
                                                          .build();

        return productComparisonsRepository.save(comparison);
    }

    // 비교함에 상품 추가
    // - 이미 담긴 상품이면 예외 발생
    @Transactional
    public ProductComparisonItems addItem(Long comparisonId, Long productId) {
        if (productComparisonItemsRepository.existsByComparison_ComparisonIdAndProduct_ProductId(comparisonId, productId)) {
            throw new IllegalStateException("이미 비교함에 담긴 상품입니다.");
        }

        ProductComparisons comparison = productComparisonsRepository.findById(comparisonId)
                                                                    .orElseThrow(() -> new IllegalArgumentException("비교함을 찾을 수 없습니다."));

        FinancialProducts product = financialProductsRepository.findById(productId)
                                                               .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        ProductComparisonItems item = ProductComparisonItems.builder()
                                                            .comparison(comparison)
                                                            .product(product)
                                                            .build();

        return productComparisonItemsRepository.save(item);
    }

    // 비교함에서 상품 삭제
    @Transactional
    public void removeItem(Long comparisonItemId) {
        productComparisonItemsRepository.deleteById(comparisonItemId);
    }

    // 비교함 상세 조회 - 담긴 상품들을 금리/윟머등급/가입기간 나란히 비교할 수 있는 형태로 조회
    @Transactional(readOnly=true)
    public ComparisonDetailResponseDTO getComparisonDetail(Long comparisonId) {
        ProductComparisons comparison = productComparisonsRepository.findById(comparisonId)
                                                                    .orElseThrow(() -> new IllegalArgumentException("비교함을 찾을 수 없습니다."));

        List<ProductComparisonItems> items = productComparisonItemsRepository.findByComparison_ComparisonId(comparisonId);

        List<ProductCompareItemDTO> itemDTOs = items.stream()
                                                    .map(this::toCompareItemDTO)
                                                    .toList();

        return ComparisonDetailResponseDTO.builder()
                                          .comparsionId(comparison.getComparisonId())
                                          .comparisonName(comparison.getComparisonName())
                                          .items(itemDTOs)
                                          .build();
    }

    // 사용자가 만든 비교함 목록 조회(여러 개의 비교함 관리)
    @Transactional(readOnly=true)
    public List<ProductComparisons> getUserComparisons(Long userId) {
        return productComparisonsRepository.findByUser_UserId(userId);
    }

    private ProductCompareItemDTO toCompareItemDTO(ProductComparisonItems item) {
        FinancialProducts p = item.getProduct();
        return ProductCompareItemDTO.builder()
                                    .comparisonItemId(item.getComparisonItemId())
                                    .productId(p.getProductId())
                                    .productName(p.getProductName())
                                    .institutionName(p.getInstitution().getInstitutionName())
                                    .productType(p.getProductType())
                                    .riskLevel(p.getRiskLevel())
                                    .expectedReturnRate(p.getExpectedReturnRate())
                                    .subscriptionPeriod(p.getSubscriptionPeriod())
                                    .principalProtection(p.isPrincipalProtection())
                                    .build();
    }
}
