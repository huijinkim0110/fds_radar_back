package fds.radar.repository.financialProduct;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import fds.radar.entity.financialProduct.ProductRateHistories;

public interface ProductRateHistoriesRepository extends JpaRepository<ProductRateHistories, Long> {
    // 상품의 금리 변경이력 전체 조회
    List<ProductRateHistories> findByProduct_ProductIdOrderByEffectiveStartDateDesc(Long productId);
}