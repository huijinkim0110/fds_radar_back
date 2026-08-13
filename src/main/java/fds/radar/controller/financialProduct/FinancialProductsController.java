package fds.radar.controller.financialProduct;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fds.radar.common.ProductType;
import fds.radar.common.RiskLevel;
import fds.radar.dto.financialProduct.ProductDetailResponseDTO;
import fds.radar.dto.financialProduct.ProductListResponseDTO;
import fds.radar.service.financialProduct.FinancialProductsService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class FinancialProductsController {
    
    private final FinancialProductsService financialProductsService;

    // 상품 목록 조회
    @GetMapping
    public ResponseEntity<Page<ProductListResponseDTO>> getProducts(
           @RequestParam(required=false) ProductType productType,
           @RequestParam(required=false) RiskLevel riskLevel,
           Pageable pageable) {
        
        Page<ProductListResponseDTO> result = financialProductsService.getProducts(productType, riskLevel, pageable);
        return ResponseEntity.ok(result);
    }

    // 상품 상세 조회
    @GetMapping("/{productId}")
    public ResponseEntity<ProductDetailResponseDTO> getProductDetail(@PathVariable Long productId) {
        ProductDetailResponseDTO result = financialProductsService.getProductDetail(productId);
        return ResponseEntity.ok(result);
    }
}
