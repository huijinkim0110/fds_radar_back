package fds.radar.controller.recommendation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fds.radar.dto.recommendation.AiRecommendationResponseDTO;
import fds.radar.dto.recommendation.RecommendationRequestDTO;
import fds.radar.service.recommendation.AiRecommendationService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/recommendations")
@RequiredArgsConstructor
public class AiRecommendationController {
    
    private final AiRecommendationService aiRecommendationService;

    // 증권 추천 요청
    // POST /recommendations/securities
    @PostMapping("/securities")
    public ResponseEntity<AiRecommendationResponseDTO> getSecuritiesRecommendation(
            @RequestBody RecommendationRequestDTO dto) {
        
        AiRecommendationResponseDTO result = aiRecommendationService.getSecuritiesRecommendation(dto);
        return ResponseEntity.ok(result);
    }
}
