package fds.radar.service.financialProduct;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fds.radar.dto.financialProduct.ProductListResponseDTO;
import fds.radar.entity.financialProduct.FavoriteProducts;
import fds.radar.entity.financialProduct.FinancialProducts;
import fds.radar.entity.user.Users;
import fds.radar.repository.financialProduct.FavoriteProductsRepository;
import fds.radar.repository.financialProduct.FinancialProductsRepository;
import fds.radar.repository.user.UserRepository;
import fds.radar.mapper.FinancialProductMapper;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FavoriteProductsService {
    
    private final FavoriteProductsRepository favoriteProductsRepository;
    private final FinancialProductsRepository financialProductsRepository;
    private final UserRepository userRepository;

    // 관심상품 등록
    // - 이미 등록된 상품이면 예외 발생
    @Transactional
    public FavoriteProducts addFavorite(Long userId, Long productId) {
        if (favoriteProductsRepository.existsByUser_UserIdAndProduct_ProductId(userId, productId)) {
            throw new IllegalStateException("이미 관심상품으로 등록된 상품입니다.");
        }

        Users user = userRepository.findById(userId)
                                   .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        FinancialProducts product = financialProductsRepository.findById(productId)
                                                               .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        FavoriteProducts favorite = FavoriteProducts.builder()
                                                    .user(user)
                                                    .product(product)
                                                    .regDate(LocalDateTime.now())
                                                    .build();

        return favoriteProductsRepository.save(favorite);
    }

    // 관심상품 해제
    // - 등록 안되어 있으면 예외 발생
    @Transactional
    public void removeFavorite(Long userId, Long productId) {
        FavoriteProducts favorite = favoriteProductsRepository
                                    .findByUser_UserIdAndProduct_ProductId(userId, productId)
                                    .orElseThrow(() -> new IllegalArgumentException("등록된 관심상품이 아닙니다."));

        favoriteProductsRepository.delete(favorite);
    }

    // 마이페이지 - 관심상품 목록 조회
    @Transactional(readOnly=true)
    public List<ProductListResponseDTO> getFavorites(Long userId) {
        List<FavoriteProducts> favorites = favoriteProductsRepository.findByUser_UserId(userId);

        return favorites.stream()
                        .map(fav -> FinancialProductMapper.toListDTO(fav.getProduct()))
                        .toList();
    }

    // 상품 상세/목록 페이지에서 "이미 관심상품 등록됨" 표시할 때 사용
    @Transactional(readOnly=true)
    public boolean isFavorite(Long userId, Long productId) {
        return favoriteProductsRepository.existsByUser_UserIdAndProduct_ProductId(userId, productId);
    }

}
