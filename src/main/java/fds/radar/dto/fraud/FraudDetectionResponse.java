package fds.radar.dto.fraud;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import fds.radar.common.PredictedFraudType;
import fds.radar.common.PredictedResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FraudDetectionResponse {
    private Long detectionResultId;
    private Long transactionId;
    private Long modelId;
    private BigDecimal fraudProbability;
    private PredictedResult predictedResult;
    private PredictedFraudType fraudType;
    private String detectionReason;
    private BigDecimal userPatternScore;
    private LocalDateTime detectedAt;
}