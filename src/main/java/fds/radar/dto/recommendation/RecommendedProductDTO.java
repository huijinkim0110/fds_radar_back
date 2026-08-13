package fds.radar.dto.recommendation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecommendedProductDTO {
    private Long productId;
    private String productName;
    private Double score;
    private String source; // "AI_SECURITIES", "AI_INSURANCE", "RULE_BASED"
}
