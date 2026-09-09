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
        fds.radar.common.PredictedFraudType fraudType = inferFraudType(transactionData, isAnomaly);

        return FraudPrediction.builder()
                .fraudProbability(java.math.BigDecimal.valueOf(response.getProbability()))
                .predictedResult(isAnomaly
                        ? fds.radar.common.PredictedResult.FRAUD
                        : fds.radar.common.PredictedResult.NORMAL)
                .fraudType(fraudType)
                .detectionReason(buildDetectionReason(transactionData, isAnomaly, fraudType, modelLabel))
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
        return fds.radar.common.PredictedFraudType.OTHER_FRAUD_TYPE;
    }

    // 관리자가 "왜 이상거래로 판단했는지" 이해할 수 있도록, inferFraudType()과 같은 판단 근거를
    // 문장으로 풀어서 보여줌. AI 서버는 확률만 주고 설명은 안 주기 때문에, 여기서 쓰는 근거는
    // inferFraudType()이 실제로 사용하는 규칙(해외거래 여부)을 그대로 문장화한 것.
    private String buildDetectionReason(TransactionData data, boolean isAnomaly,
                                         fds.radar.common.PredictedFraudType fraudType, String modelLabel) {
        if (!isAnomaly) {
            return modelLabel + " 모델이 정상 거래로 판단했습니다.";
        }
        if (fraudType == fds.radar.common.PredictedFraudType.STOLEN_CARD) {
            return "평소 이용 이력이 없는 해외 국가(" + data.getCountryCode() + ")에서 거래가 발생하여 도난·분실 카드 사용이 의심됩니다.";
        }
        return modelLabel + " 모델이 거래 패턴을 분석한 결과 이상거래 가능성이 높게 나타났습니다.";
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