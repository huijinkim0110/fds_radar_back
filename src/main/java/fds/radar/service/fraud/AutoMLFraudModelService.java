package fds.radar.service.fraud;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import fds.radar.dto.fraud.AiPredictRequest;
import fds.radar.dto.fraud.AiPredictResponse;
import fds.radar.service.fraud.vo.FraudPrediction;
import fds.radar.service.fraud.vo.TransactionData;

/**
 * FastAPI로 띄운 AutoML 예측 서버를 호출하는 실제 구현체.
 * MockFraudModelService 대신 이걸 @Service로 활성화하면
 * FraudDetectionService 코드는 한 줄도 안 건드리고 실제 모델로 전환됨.
 */
@Service
public class AutoMLFraudModelService implements FraudModelService {

    private final RestClient restClient;

    public AutoMLFraudModelService(@Value("${ai.server.url}") String aiServerUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(aiServerUrl)
                // requestFactory를 SimpleClientHttpRequestFactory로 명시하는 이유:
                // RestClient가 기본으로 쓰는 HTTP 클라이언트가 HTTP/2 프로토콜로
                // 먼저 연결을 시도하는데, FastAPI를 띄우는 uvicorn 서버는 HTTP/1.1만
                // 지원해서 "Unsupported upgrade request" 에러가 남 (실제로 발생했던 에러).
                // SimpleClientHttpRequestFactory는 자바 표준 HttpURLConnection 기반이라
                // 애초에 HTTP/1.1만 쓰고 HTTP/2 시도 자체를 안 해서 이 문제를 피할 수 있음.
                .requestFactory(new org.springframework.http.client.SimpleClientHttpRequestFactory())
                .build();
    }

    @Override
    public FraudPrediction predict(TransactionData transactionData) {
        AiPredictRequest request = toAiRequest(transactionData);

        AiPredictResponse response = restClient.post()
                .uri("/predict")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(AiPredictResponse.class);

        if (response == null) {
            throw new IllegalStateException("AI 서버로부터 응답을 받지 못했습니다.");
        }

        return toFraudPrediction(response, transactionData);
    }

    private FraudPrediction toFraudPrediction(AiPredictResponse response, TransactionData transactionData) {
    String modelLabel = transactionData.getTransactionType() == fds.radar.common.TransactionType.CARD_PAYMENT
            ? "AutoML"
            : "HistGradientBoosting";

    boolean isAnomaly = response.isAnomaly();
    double probability = response.getProbability();
    fds.radar.common.PredictedFraudType fraudType = inferFraudType(transactionData, isAnomaly, probability);

    return FraudPrediction.builder()
            .fraudProbability(java.math.BigDecimal.valueOf(probability))
            .predictedResult(isAnomaly
                    ? fds.radar.common.PredictedResult.FRAUD
                    : fds.radar.common.PredictedResult.NORMAL)
            .fraudType(fraudType)
            .detectionReason(buildDetectionReason(transactionData, isAnomaly, fraudType, modelLabel))
            .build();
}

private fds.radar.common.PredictedFraudType inferFraudType(TransactionData data, boolean isAnomaly, double probability) {
    if (!isAnomaly) {
        return null;
    }

    boolean isHighConfidence = probability >= 0.6; // AI 확신도를 "고액 등 뚜렷한 신호" 기준으로 사용

    if (data.getTransactionType() == fds.radar.common.TransactionType.CARD_PAYMENT) {
        boolean isForeign = data.getCountryCode() != null && !"KR".equalsIgnoreCase(data.getCountryCode());
        if (isForeign && isHighConfidence) {
            return fds.radar.common.PredictedFraudType.STOLEN_CARD;
        }
    } else if (data.getTransactionType() == fds.radar.common.TransactionType.ACCOUNT_TRANSFER) {
        if (data.isNewRecipient() && isHighConfidence) {
            return fds.radar.common.PredictedFraudType.UNUSUAL_TRANSFER;
        }
    }

    return fds.radar.common.PredictedFraudType.OTHER_FRAUD_TYPE;
}

private String buildDetectionReason(TransactionData data, boolean isAnomaly,
                                     fds.radar.common.PredictedFraudType fraudType, String modelLabel) {
    if (!isAnomaly) {
        return modelLabel + " 모델이 정상 거래로 판단했습니다.";
    }
    if (fraudType == fds.radar.common.PredictedFraudType.STOLEN_CARD) {
        return "평소 이용 이력이 없는 해외 국가(" + data.getCountryCode() + ")에서 고액 결제가 발생하여 도난·분실 카드 사용이 의심됩니다.";
    }
    if (fraudType == fds.radar.common.PredictedFraudType.UNUSUAL_TRANSFER) {
        return "이전에 거래한 적 없는 신규 수취인에게 고액이 이체되어 이상 송금이 의심됩니다.";
    }
    return "모델이 거래 패턴을 분석한 결과 이상거래 가능성이 높게 나타났습니다.";
}
    
    private AiPredictRequest toAiRequest(TransactionData data) {
        return AiPredictRequest.builder()
                .transactionType(data.getTransactionType() != null ? data.getTransactionType().name() : null)
                .amount(data.getAmount())
                .occurredAt(data.getOccurredAt() != null ? data.getOccurredAt().toString() : null)
                .transactionChannel(data.getTransactionChannel() != null ? data.getTransactionChannel().name() : null)
                .countryCode(data.getCountryCode())
                .merchantName(data.getMerchantName())
                .newRecipient(data.isNewRecipient())
                .merchantCategory(data.getMerchantCategory() != null ? data.getMerchantCategory().name() : null)
                .build();
    }

}