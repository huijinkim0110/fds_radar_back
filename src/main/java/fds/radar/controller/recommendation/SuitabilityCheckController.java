package fds.radar.controller.recommendation;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fds.radar.dto.recommendation.SuitabilityCheckRequestDTO;
import fds.radar.entity.recommendation.SuitabilityChecks;
import fds.radar.service.recommendation.SuitabilityCheckService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/suitability-checks")
@RequiredArgsConstructor
public class SuitabilityCheckController {
    
    private final SuitabilityCheckService suitabilityCheckService;

    // 적합성 검사 실행(상품 상세 페이지에서 호출)
    @PostMapping
    public ResponseEntity<SuitabilityChecks> checkSuitability(@RequestBody SuitabilityCheckRequestDTO dto) {
        SuitabilityChecks result = suitabilityCheckService.checkSuitability(dto);
        return ResponseEntity.ok(result);
    }

    // 특정 사용자-상품 조합의 검사 이력 조회(재검사 이력 확인용)
    // GET /suitability-checks?userId=1&productId=5
    @GetMapping
    public ResponseEntity<List<SuitabilityChecks>> getCheckHistory(
           @RequestParam Long userId,
           @RequestParam Long productId) {
        
        List<SuitabilityChecks> result = suitabilityCheckService.getCheckHistory(userId, productId);
        return ResponseEntity.ok(result);
    }
}
