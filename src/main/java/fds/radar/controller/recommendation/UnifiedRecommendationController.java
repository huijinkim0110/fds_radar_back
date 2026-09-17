package fds.radar.controller.recommendation;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fds.radar.dto.recommendation.RecommendationRequestDTO;
import fds.radar.dto.recommendation.UnifiedRecommendationResponseDTO;
import fds.radar.entity.recommendation.RecommendationItems;
import fds.radar.entity.recommendation.RecommendationResults;
import fds.radar.service.recommendation.UnifiedRecommendationService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/recommendations")
@RequiredArgsConstructor
public class UnifiedRecommendationController {
    
    private final UnifiedRecommendationService unifiedRecommendationService;

    // 증권/보험/예적금 통합 추천
    @PostMapping("/unified")
    public ResponseEntity<UnifiedRecommendationResponseDTO> getUnifiedRecommendations(
            @AuthenticationPrincipal Long userId,
            @RequestBody RecommendationRequestDTO dto) {

        UnifiedRecommendationResponseDTO result = unifiedRecommendationService.getUnifiedRecommendations(userId, dto);
        return ResponseEntity.ok(result);
    }

    // 추천 이력 목록 조회(마이페이지 - 이전 추천 리스트)
    // GET /recommendations/history?userId=1
    @GetMapping("/history")
    public ResponseEntity<List<RecommendationResults>> getHistory(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(unifiedRecommendationService.getHistory(userId));
    }

    // 특정 추천 이력에 속한 상품 목록 조회
    @GetMapping("/history/{recommendationResultId}/items")
    public ResponseEntity<List<RecommendationItems>> getHistoryItems(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long recommendationResultId) {
        return ResponseEntity.ok(unifiedRecommendationService.getHistoryItems(userId, recommendationResultId));
    }
}
