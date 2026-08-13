package fds.radar.dto.recommendation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiRecommendationItemDTO {
    // FastAPI 응답 안의 추천 항목 하나
    private String product_name;
    private Double score;
}
