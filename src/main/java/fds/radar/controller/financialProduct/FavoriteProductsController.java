package fds.radar.controller.financialProduct;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fds.radar.dto.financialProduct.ProductListResponseDTO;
import fds.radar.entity.financialProduct.FavoriteProducts;
import fds.radar.service.financialProduct.FavoriteProductsService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/favorite-products")
@RequiredArgsConstructor
public class FavoriteProductsController {
    
    private final FavoriteProductsService favoriteProductsService;

    // 관심상품 등록
    @PostMapping
    public ResponseEntity<FavoriteProducts> addFavorite(
           @RequestParam Long userId,
           @RequestParam Long productId) {
        
        FavoriteProducts result = favoriteProductsService.addFavorite(userId, productId);
        return ResponseEntity.ok(result);
    }

    // 관심상품 해제
    // DELETE /favorite-products?userId=1&productId=5
    @DeleteMapping
    public ResponseEntity<Void> removeFavorite(
           @RequestParam Long userId,
           @RequestParam Long productId) {
           
        favoriteProductsService.removeFavorite(userId, productId);        
        return ResponseEntity.noContent().build();
    }

    // 마이페이지 - 관심상품 목록 조회
    // GET /favorite-products?userId=1
    @GetMapping
    public ResponseEntity<List<ProductListResponseDTO>> getFavorites(@RequestParam Long userId) {
        List<ProductListResponseDTO> result = favoriteProductsService.getFavorites(userId);
        return ResponseEntity.ok(result);
    }

    // 상품 상세 페이지 - 이미 관심상품 등록됐는지 여부 확인
    // GET /favorite-products/check?userId=1&productId=5
    @GetMapping("/check")
    public ResponseEntity<Boolean> isFavorite(
           @RequestParam Long userId,
           @RequestParam Long productId) {

        boolean result = favoriteProductsService.isFavorite(userId, productId);
        return ResponseEntity.ok(result);
    }
}
