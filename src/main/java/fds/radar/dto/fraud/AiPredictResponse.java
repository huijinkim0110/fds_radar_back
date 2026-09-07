package fds.radar.dto.fraud;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * FastAPI 예측 서버(/predict)로부터 받는 응답 형식.
 * FastAPI 쪽 schemas.py의 PredictionResponse({probability, isAnomaly})와 대응.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiPredictResponse {
    private double probability;
    private boolean isAnomaly;
}