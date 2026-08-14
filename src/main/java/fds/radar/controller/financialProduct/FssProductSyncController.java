// package fds.radar.controller.financialProduct;

// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import fds.radar.service.financialProduct.FssProductSyncService;
// import lombok.RequiredArgsConstructor;

// @RestController
// @RequestMapping("/admin/products")
// @RequiredArgsConstructor
// // 실행용 임시 Controller
// public class FssProductSyncController {
    
//     private final FssProductSyncService fssProductSyncService;

//     @PostMapping("/sync-deposits")
//     public ResponseEntity<String> syncDeposits() {
//         int count = fssProductSyncService.syncDeposits();
//         return ResponseEntity.ok(count + "개 정기예금 상품 저장 완료");
//     }

//     @PostMapping("/sync-savings")
//     public ResponseEntity<String> syncSavings() {
//         int count = fssProductSyncService.syncSavings();
//         return ResponseEntity.ok(count + "개 적금 상품 저장 완료");
//     }
// }
