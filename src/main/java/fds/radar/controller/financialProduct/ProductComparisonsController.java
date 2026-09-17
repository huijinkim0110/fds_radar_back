package fds.radar.controller.financialProduct;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fds.radar.dto.financialProduct.ComparisonDetailResponseDTO;
import fds.radar.entity.financialProduct.ProductComparisonItems;
import fds.radar.entity.financialProduct.ProductComparisons;
import fds.radar.service.financialProduct.ProductComparisonsService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/product-comparisons")
@RequiredArgsConstructor
public class ProductComparisonsController {
    
    private final ProductComparisonsService productComparisonsService;

    // 비교함 생성

    @PostMapping
    public ResponseEntity<ProductComparisons> createComparison(
           @AuthenticationPrincipal Long userId,
           @RequestParam String comparisonName) {

        ProductComparisons result = productComparisonsService.createComparison(userId, comparisonName);
        return ResponseEntity.ok(result);
    }

    // 비교함에 상품 추가
    // POST /product-comparisons/{comparisonId}/items?productId=5
    @PostMapping("/{comparisonId}/items")
    public ResponseEntity<ProductComparisonItems> addItem(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long comparisonId,
            @RequestParam Long productId) {

        ProductComparisonItems result = productComparisonsService.addItem(userId, comparisonId, productId);
        return ResponseEntity.ok(result);
    }

    // 비교함에서 상품 삭제
    @DeleteMapping("/{comparisonId}/items/{comparisonItemId}")
    public ResponseEntity<Void> removeItem(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long comparisonId,
            @PathVariable Long comparisonItemId) {
        productComparisonsService.removeItem(userId, comparisonItemId);
        return ResponseEntity.noContent().build();
    }

    // 비교함 상세 조회(담긴 상품들 나란히 비교)
    @GetMapping("/{comparisonId}")
    public ResponseEntity<ComparisonDetailResponseDTO> getComparisonDetail(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long comparisonId) {
        ComparisonDetailResponseDTO result = productComparisonsService.getComparisonDetail(userId, comparisonId);
        return ResponseEntity.ok(result);
    }

    // 사용자가 만든 비교함 목록 조회
    @GetMapping
    public ResponseEntity<List<ProductComparisons>> getUserComparisons(@AuthenticationPrincipal Long userId) {
        List<ProductComparisons> result = productComparisonsService.getUserComparisons(userId);
        return ResponseEntity.ok(result);
    }

    // 비교함 이름 저장(변경) - 담긴 상품 유지
    @PatchMapping("/{comparisonId}")
    public ResponseEntity<ProductComparisons> renameComparison(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long comparisonId,
            @RequestParam String comparisonName) {
        
        ProductComparisons result = productComparisonsService.renameComparison(userId, comparisonId, comparisonName);
        return ResponseEntity.ok(result);
    }
}