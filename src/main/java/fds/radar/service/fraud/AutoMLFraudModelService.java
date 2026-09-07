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

        return FraudPrediction.builder()
                .fraudProbability(java.math.BigDecimal.valueOf(response.getProbability()))
                .predictedResult(isAnomaly
                        ? fds.radar.common.PredictedResult.FRAUD
                        : fds.radar.common.PredictedResult.NORMAL)
                .fraudType(inferFraudType(transactionData, isAnomaly))
                .detectionReason(modelLabel + " 모델(FastAPI) 예측 결과")
                .build();
    }

    // AI 서버(FastAPI)가 이상확률만 주고 유형까지는 분류해주지 않아서,
    // 이상거래로 판정된 건에 한해 거래 데이터 특징으로 유형을 추정함
    // (관리자 상세화면 "이상유형" 표시 + 이상거래 분석 통계의 유형별 분포에 사용됨)
    private fds.radar.common.PredictedFraudType inferFraudType(TransactionData data, boolean isAnomaly) {
        if (!isAnomaly) {
            return null;
        }
        boolean isForeign = data.getCountryCode() != null && !"KR".equalsIgnoreCase(data.getCountryCode());
        if (isForeign) {
            return fds.radar.common.PredictedFraudType.STOLEN_CARD;
        }
        if (data.isNewRecipient()) {
            return fds.radar.common.PredictedFraudType.UNUSUAL_TRANSFER;
        }
        return fds.radar.common.PredictedFraudType.OTHER_FRAUD_TYPE;
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