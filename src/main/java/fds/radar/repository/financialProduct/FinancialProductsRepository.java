package fds.radar.repository.financialProduct;

import fds.radar.entity.financialProduct.FinancialProducts;
import fds.radar.common.ProductType;
import fds.radar.common.RiskLevel;
import fds.radar.common.ProductStatus;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FinancialProductsRepository extends JpaRepository<FinancialProducts, Long> {
    // 상품 목록 조회용 - productType/riskLevel은 선택적 필터(null이면 조건 무시)
    // 판매중(ON_SALE)인 상품만 목록에 노출
    @Query("SELECT fp FROM FinancialProducts fp " +
           "WHERE fp.productStatus = :status " + 
           "AND (:productType IS NULL OR fp.productType = :productType) " + 
           "AND (:riskLevel IS NULL OR fp.riskLevel = :riskLevel)")
    Page<FinancialProducts> search(
        @Param("productType") ProductType productType,
        @Param("riskLevel") RiskLevel riskLevel,
        @Param("status") ProductStatus status,
        Pageable pageable);

    // 여러 상품 유형 + 여러 위험등급 동시에 필터링해서 조회
    List<FinancialProducts> findByProductTypeInAndRiskLevelIn(
        List<ProductType> productTypes, Set<RiskLevel> riskLevels);

    // 상품 이름으로 검색
    Optional<FinancialProducts> findByProductName(String productName);
        
}