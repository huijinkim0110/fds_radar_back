package fds.radar.repository.financialProduct;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import fds.radar.entity.financialProduct.ProductComparisonItems;

public interface ProductComparisonItemsRepository extends JpaRepository<ProductComparisonItems, Long> {
    // 특정 비교함에 담긴 상품 목록 전체 조회
    // - 상품 비교 화면에서 금리/위험등급/기간을 나란히 보여줄 때 사용
    List<ProductComparisonItems> findByComparison_ComparisonId(Long comparisonId);

    // 같은 비교함에 같은 상품이 이미 담겼는지 확인
    boolean existsByComparison_ComparisonIdAndProduct_ProductId(Long comparisonId, Long productId);

    // 삭제 시 본인 비교함 소유 검증용 - comparisonItemId로 거슬러 올라가 유저 확인
    Optional<ProductComparisonItems> findByComparisonItemIdAndComparison_User_UserId(Long comparisonItemId, Long userId);
}
