package fds.radar.repository.financialProduct;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import fds.radar.entity.financialProduct.FavoriteProducts;

public interface FavoriteProductsRepository extends JpaRepository<FavoriteProducts, Long> {
    // 관심상품 조회
    List<FavoriteProducts> findByUser_UserId(Long userId);

    // 특정 상품이 이미 관심상품으로 등록되어 있는지 조회
    // - 상품 상세 페이지 진입 시 "관심상품 등록됨" 여부 표시 or 해제 대상 찾을 때 사용
    Optional<FavoriteProducts> findByUser_UserIdAndProduct_ProductId(Long userId, Long productId);

    // 한 사용자가 동일한 상품에 대해 중복 선택 방지
    boolean existsByUser_UserIdAndProduct_ProductId(Long userId, Long productId);
}
