package fds.radar.dto.recommendation;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiRecommendationResponseDTO {
    // FastAPI 응답 전체(추천 목록)
    private List<AiRecommendationItemDTO> recommendations;
}
