package fds.radar.controller.recommendation;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fds.radar.dto.recommendation.RecommendationRequestDTO;
import fds.radar.dto.recommendation.RecommendedProductDTO;
import fds.radar.service.recommendation.UnifiedRecommendationService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/recommendations")
@RequiredArgsConstructor
public class UnifiedRecommendationController {
    
    private final UnifiedRecommendationService unifiedRecommendationService;

    // 증권/보험/예적금 통합 추천
    // POST /recommendations/unified
    @PostMapping("/unified")
    public ResponseEntity<List<RecommendedProductDTO>> getUnifiedRecommendations(
            @RequestBody RecommendationRequestDTO dto) {

        List<RecommendedProductDTO> result = unifiedRecommendationService.getUnifiedRecommendations(dto);
        return ResponseEntity.ok(result);
    }
}
