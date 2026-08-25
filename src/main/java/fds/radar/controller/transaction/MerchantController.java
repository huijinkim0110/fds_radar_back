// MerchantController
package fds.radar.controller.transaction;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import fds.radar.dto.transaction.MerchantCreateRequest;
import fds.radar.dto.transaction.MerchantResponse;
import fds.radar.dto.transaction.MerchantRiskUpdateRequest;
import fds.radar.service.transaction.MerchantService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/merchants")
@CrossOrigin(origins = "*")
public class MerchantController {

    private final MerchantService merchantService;

    public MerchantController(MerchantService merchantService) {
        this.merchantService = merchantService;
    }

    // 가맹점 등록 (ADMIN)
    @PostMapping
    public ResponseEntity<MerchantResponse> create(
            @Valid @RequestBody MerchantCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(merchantService.create(request));
    }

    // 가맹점 목록
    @GetMapping
    public ResponseEntity<List<MerchantResponse>> getMerchants() {
        return ResponseEntity.ok(merchantService.getMerchants());
    }

    // 가맹점 상세
    @GetMapping("/{merchantId}")
    public ResponseEntity<MerchantResponse> getMerchant(@PathVariable Long merchantId) {
        return ResponseEntity.ok(merchantService.getMerchant(merchantId));
    }

    // 위험상태 변경 (ADMIN)
    @PatchMapping("/{merchantId}/risk")
    public ResponseEntity<MerchantResponse> updateRisk(
            @PathVariable Long merchantId,
            @Valid @RequestBody MerchantRiskUpdateRequest request) {
        return ResponseEntity.ok(merchantService.updateRisk(merchantId, request));
    }
}