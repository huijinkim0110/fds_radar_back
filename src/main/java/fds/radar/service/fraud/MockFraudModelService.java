package fds.radar.service.fraud;

import java.math.BigDecimal;

import fds.radar.common.PredictedFraudType;
import fds.radar.common.PredictedResult;
import fds.radar.service.fraud.vo.FraudPrediction;
import fds.radar.service.fraud.vo.TransactionData;

// @Service (AutoMLFraudModelService로 전환 시 주석 처리)
public class MockFraudModelService implements FraudModelService {

    private static final BigDecimal HIGH_AMOUNT_THRESHOLD = new BigDecimal("1000000");

    @Override
    public FraudPrediction predict(TransactionData transactionData) {
        BigDecimal probability = new BigDecimal("0.10");
        StringBuilder reasonBuilder = new StringBuilder();
        PredictedFraudType fraudType = null;

        boolean isForeign = transactionData.getCountryCode() != null
                && !"KR".equalsIgnoreCase(transactionData.getCountryCode());
        boolean isHighAmount = transactionData.getAmount() != null
                && transactionData.getAmount().compareTo(HIGH_AMOUNT_THRESHOLD) >= 0;
        boolean isNewRecipient = transactionData.isNewRecipient();

        if (isForeign) {
            probability = probability.add(new BigDecimal("0.40"));
            reasonBuilder.append("해외(").append(transactionData.getCountryCode()).append(") 거래. ");
            fraudType = PredictedFraudType.STOLEN_CARD;
        }
        if (isHighAmount) {
            probability = probability.add(new BigDecimal("0.30"));
            reasonBuilder.append("평소 대비 고액 거래(").append(transactionData.getAmount()).append("원). ");
            if (isForeign) {
                fraudType = PredictedFraudType.STOLEN_CARD;
            }
        }
        if (isNewRecipient) {
            probability = probability.add(new BigDecimal("0.20"));
            reasonBuilder.append("신규 수취계좌로의 거래. ");
            fraudType = PredictedFraudType.UNUSUAL_TRANSFER;
        }

        if (probability.compareTo(new BigDecimal("0.99")) > 0) {
            probability = new BigDecimal("0.99");
        }

        boolean isAnomaly = probability.compareTo(new BigDecimal("0.70")) >= 0;

        if (!isAnomaly) {
            reasonBuilder.setLength(0);
            reasonBuilder.append("Mock 모델 - 정상 패턴 범위 내 거래");
            fraudType = null;
        }

        return FraudPrediction.builder()
                .fraudProbability(probability)
                .predictedResult(isAnomaly ? PredictedResult.FRAUD : PredictedResult.NORMAL)
                .fraudType(fraudType)
                .detectionReason(reasonBuilder.toString().trim())
                .userPatternScore(new BigDecimal("0.50"))
                .build();
    }
}