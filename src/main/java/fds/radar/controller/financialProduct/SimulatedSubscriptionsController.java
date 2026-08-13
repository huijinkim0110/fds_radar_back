package fds.radar.controller.financialProduct;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    // POST /simulated-subscriptions
    @PostMapping
    public ResponseEntity<SubscriptionResponseDTO> subscribe(@RequestBody SubscriptionRequestDTO dto) {
        SubscriptionResponseDTO result = simulatedSubscriptionsService.subscribe(dto);
        return ResponseEntity.ok(result);
    }

    // 모의 가입 취소
    // PATCh /simulated-subscriptions/{simulatedSubscriptionId}/cancel
    @PatchMapping("/{simulatedSubscriptionId}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable Long simulatedSubscriptionId) {
        simulatedSubscriptionsService.cancel(simulatedSubscriptionId);
        return ResponseEntity.noContent().build();
    }

    // 가상 포트폴리오 조회
    // GET /simulated-subscriptions?userId=1
    @GetMapping
    public ResponseEntity<List<SubscriptionResponseDTO>> getPortfolio(@RequestParam Long userId) {
        List<SubscriptionResponseDTO> result = simulatedSubscriptionsService.getPortfolio(userId);
        return ResponseEntity.ok(result);
    }
}
