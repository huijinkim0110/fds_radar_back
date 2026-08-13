package fds.radar.service.fraud;

import java.math.BigDecimal;

import fds.radar.common.PredictedFraudType;
import fds.radar.common.PredictedResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * AI 모델(AutoML)이 예측한 "순수 결과"만 담는 내부 전용 객체.
 *
 * DTO(dto/fraud 패키지)가 아닌 이유:
 *   - 이 객체는 API 요청/응답으로 절대 외부에 노출되지 않음.
 *   - AI 모델 예측 → DB 저장(FraudDetectionResults) → 클라이언트 응답(FraudDetectionResponse)
 *     으로 이어지는 변환 과정에서, "모델이 방금 뱉은 원본 결과"만 잠깐 담아두는 완충(buffer) 역할.
 *
 * 이렇게 분리해두는 이유:
 *   - 지금은 MockFraudModelService를 쓰다가, 나중에 실제 AutoML API로 교체할 때
 *     FraudModelService 인터페이스의 반환 타입(FraudPrediction)만 유지하면
 *     Service/Controller 쪽 코드는 전혀 건드릴 필요가 없음.
 *   - AI 모델의 원본 응답 형식이 Entity나 DTO 구조에 직접 섞여 들어가는 걸 막아줌.
 *
 * @Setter가 없는 이유:
 *   - AI가 한 번 예측한 결과는 이후에 임의로 변경되면 안 되는 값이라
 *     생성 시(@Builder)에만 값을 채우고 이후로는 읽기(Getter)만 허용하는 불변 객체로 설계함.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FraudPrediction {
    private BigDecimal fraudProbability;
    private PredictedResult predictedResult;
    private PredictedFraudType fraudType;
    private String detectionReason;
    private BigDecimal userPatternScore;
}