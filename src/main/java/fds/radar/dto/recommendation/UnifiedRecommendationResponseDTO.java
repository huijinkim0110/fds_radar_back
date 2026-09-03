package fds.radar.dto.recommendation;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnifiedRecommendationResponseDTO {
    private Long recommendationResultId;
    private List<RecommendedProductDTO> results;
    private boolean goalMissing; // 재무목표 없이 계산됨 -> 프론트에서 정확도 경고 표시
    private LocalDateTime requestedAt;
}
