package fds.radar.service.fraud;

/**
 * AI 이상거래 탐지 모델을 호출하는 역할의 인터페이스.
 *
 * 지금은 MockFraudModelService(가짜 결과 반환)를 구현체로 쓰고,
 * 나중에 실제 AutoML 모델이 준비되면 AutoMLFraudModelService 같은
 * 새 구현체를 만들어서 갈아끼우기만 하면 됨.
 * → 이 인터페이스를 사용하는 다른 코드(FraudDetectionServiceImpl 등)는
 *   구현체가 바뀌어도 전혀 수정할 필요가 없음.
 */
public interface FraudModelService {

    // transactionId 하나로 거래정보를 조회해서 AI 입력값으로 변환 → 모델 호출 → 예측 결과 반환
    // (거래정보 조회 로직은 2차에서 구현)
    FraudPrediction predict(Long transactionId);
}