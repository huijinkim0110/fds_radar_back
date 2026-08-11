package fds.radar.repository.financialProduct;

import fds.radar.entity.financialProduct.FinancialProducts;
import fds.radar.common.ProductType;
import fds.radar.common.RiskLevel;
import fds.radar.common.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FinancialProductsRepository extends JpaRepository<FinancialProducts, Long> {
    // 카테고리별 필터링 + 페이징 (상품 목록 조회 기능용)
    Page<FinancialProducts> findByProductTypeAndProductStatus(ProductType productType, ProductStatus status, Pageable pageable);
    Page<FinancialProducts> findByRiskLevelAndProductStatus(RiskLevel riskLevel, ProductStatus status, Pageable pageable);
    Page<FinancialProducts> findByProductStatus(ProductStatus status, Pageable pageable);
}