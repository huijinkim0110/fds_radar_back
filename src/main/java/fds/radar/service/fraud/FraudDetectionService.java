package fds.radar.service.fraud;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import fds.radar.common.ModelType;
import fds.radar.dto.fraud.FraudDetectionRequest;
import fds.radar.dto.fraud.FraudDetectionResponse;
import fds.radar.entity.dispute.AiModels;
import fds.radar.entity.fraud.FraudDetectionResults;
import fds.radar.entity.transaction.Transactions;
import fds.radar.repository.dispute.AiModelRepository;
import fds.radar.repository.fraud.FraudDetectionResultRepository;
import fds.radar.repository.transaction.TransactionRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FraudDetectionService {

    private final FraudDetectionResultRepository fraudDetectionResultRepository;
    private final FraudModelService fraudModelService;
    private final TransactionRepository transactionRepository;
    private final AiModelRepository aiModelRepository;
    private final FraudCaseService fraudCaseService; // 4차: threshold 초과 시 사건 자동생성 위임

    // 2~4차: AI 예측 + DB 저장 + (threshold 초과 시) 사건 자동생성까지
    public FraudDetectionResponse detectFraud(FraudDetectionRequest request) {
        Long transactionId = request.getTransactionId();

        Transactions transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "존재하지 않는 거래입니다. transactionId=" + transactionId));

        TransactionData transactionData = toTransactionData(transaction);
        FraudPrediction prediction = fraudModelService.predict(transactionData);

        AiModels model = aiModelRepository
                .findFirstByModelTypeAndActiveTrueOrderByRegisteredAtDesc(ModelType.FRAUD_DETECTION)
                .orElseThrow(() -> new IllegalStateException("운영 중(active=true)인 이상거래 탐지 모델이 없습니다."));

        FraudDetectionResults saved = fraudDetectionResultRepository.save(
                FraudDetectionResults.builder()
                        .transaction(transaction)
                        .model(model)
                        .fraudProbability(prediction.getFraudProbability())
                        .predictedResult(prediction.getPredictedResult())
                        .fraudType(prediction.getFraudType())
                        .detectionReason(prediction.getDetectionReason())
                        .userPatternScore(prediction.getUserPatternScore())
                        .detectedAt(LocalDateTime.now())
                        .build()
        );

        // 4차: threshold 이상이면 FraudCaseService가 알아서 사건 생성 (아니면 아무 일도 안 일어남)
        fraudCaseService.createCaseIfNeeded(saved);

        return toResponse(saved);
    }

    // 3차: 탐지결과 ID로 단건 조회
    public FraudDetectionResponse getDetectionResult(Long detectionResultId) {
        FraudDetectionResults result = fraudDetectionResultRepository.findById(detectionResultId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "존재하지 않는 탐지결과입니다. detectionResultId=" + detectionResultId));
        return toResponse(result);
    }

    // 3차: 특정 거래의 최신 탐지결과 조회
    public FraudDetectionResponse getLatestByTransaction(Long transactionId) {
        FraudDetectionResults result = fraudDetectionResultRepository
                .findTopByTransaction_TransactionIdOrderByDetectedAtDesc(transactionId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "탐지결과가 없는 거래입니다. transactionId=" + transactionId));
        return toResponse(result);
    }

    private FraudDetectionResponse toResponse(FraudDetectionResults result) {
        return FraudDetectionResponse.builder()
                .detectionResultId(result.getDetectionResultId())
                .transactionId(result.getTransaction().getTransactionId())
                .modelId(result.getModel().getModelId())
                .fraudProbability(result.getFraudProbability())
                .predictedResult(result.getPredictedResult())
                .fraudType(result.getFraudType())
                .detectionReason(result.getDetectionReason())
                .userPatternScore(result.getUserPatternScore())
                .detectedAt(result.getDetectedAt())
                .build();
    }

    private TransactionData toTransactionData(Transactions transaction) {
        boolean newRecipient = transaction.getRecipient() != null
                && !transaction.getRecipient().isRegistered();

        String merchantName = transaction.getMerchant() != null
                ? transaction.getMerchant().getMerchantName()
                : null;

        return TransactionData.builder()
                .transactionId(transaction.getTransactionId())
                .amount(transaction.getAmount())
                .occurredAt(transaction.getOccurredAt())
                .transactionType(transaction.getTransactionType())
                .transactionChannel(transaction.getTransactionChannel())
                .countryCode(transaction.getCountryCode())
                .merchantName(merchantName)
                .newRecipient(newRecipient)
                .build();
    }
}