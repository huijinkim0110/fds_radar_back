package fds.radar.service.fraud;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import fds.radar.common.PredictedResult;

/**
 * FraudModelService의 임시 구현체.
 * 실제 AutoML 모델이 아직 없어서, 항상 정해진 값만 반환하는 가짜(Mock) 구현.
 *
 * 2~3차에서 이 값을 그대로 써서 "AI 예측 → DB 저장 → 조회" 흐름을 먼저 완성하고,
 * 나중에 진짜 AutoML API가 준비되면 AutoMLFraudModelService를 새로 만들어서
 * @Service 어노테이션만 이쪽에서 저쪽으로 옮기면 교체 끝.
 */
@Service
public class MockFraudModelService implements FraudModelService {

    @Override
    public FraudPrediction predict(Long transactionId) {
        // TODO 2차: 실제로는 transactionId로 거래정보 조회 → AI 입력값 변환 → 모델 호출
        // 지금은 항상 "정상" 판정만 반환
        return FraudPrediction.builder()
                .fraudProbability(new BigDecimal("0.10"))
                .predictedResult(PredictedResult.NORMAL)
                .fraudType(null)
                .detectionReason("Mock 모델 - 임시 정상 판정")
                .userPatternScore(new BigDecimal("0.50"))
                .build();
    }
}