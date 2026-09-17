package fds.radar.repository.financialProduct;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import fds.radar.entity.financialProduct.ProductComparisons;

public interface ProductComparisonsRepository extends JpaRepository<ProductComparisons, Long> {
    // 유저의 비교상품 찾기
    List<ProductComparisons> findByUser_UserId(Long userId);

    // 본인 소유 비교함 검증용
    Optional<ProductComparisons> findByComparisonIdAndUser_UserId(Long comparisonId, Long userId);
}
