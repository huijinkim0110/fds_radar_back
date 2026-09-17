package fds.radar.controller.financialProduct;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fds.radar.dto.financialProduct.SubscriptionRequestDTO;
import fds.radar.dto.financialProduct.SubscriptionResponseDTO;
import fds.radar.service.financialProduct.SimulatedSubscriptionsService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/simulated-subscriptions")
@RequiredArgsConstructor
public class SimulatedSubscriptionsController {
    
    private final SimulatedSubscriptionsService simulatedSubscriptionsService;

    // 상품 모의 가입
    @PostMapping
    public ResponseEntity<SubscriptionResponseDTO> subscribe(
            @AuthenticationPrincipal Long userId,
            @RequestBody SubscriptionRequestDTO dto) {
        SubscriptionResponseDTO result = simulatedSubscriptionsService.subscribe(userId, dto);
        return ResponseEntity.ok(result);
    }

    // 모의 가입 취소
    @PatchMapping("/{simulatedSubscriptionId}/cancel")
    public ResponseEntity<Void> cancel(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long simulatedSubscriptionId) {
        simulatedSubscriptionsService.cancel(userId, simulatedSubscriptionId);
        return ResponseEntity.noContent().build();
    }

    // 가상 포트폴리오 조회
    @GetMapping
    public ResponseEntity<List<SubscriptionResponseDTO>> getPortfolio(@AuthenticationPrincipal Long userId) {
        List<SubscriptionResponseDTO> result = simulatedSubscriptionsService.getPortfolio(userId);
        return ResponseEntity.ok(result);
    }
}
