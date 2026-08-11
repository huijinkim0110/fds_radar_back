package fds.radar.service.fraud;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import fds.radar.dto.fraud.FraudDetectionRequest;
import fds.radar.dto.fraud.FraudDetectionResponse;
import fds.radar.entity.transaction.Transactions;
import fds.radar.repository.transaction.TransactionRepository;
import fds.radar.repository.fraud.FraudDetectionResultRepository;
import lombok.RequiredArgsConstructor;

/**
 * 이상거래 탐지 실행 및 결과 조회를 담당하는 서비스.
 *
 * FraudModelService와 달리 이 서비스는 구현체가 바뀔 계획이 없어서
 * 인터페이스 없이 클래스 하나로 작성함 (인터페이스+Impl 분리는
 * 실제로 구현체를 교체할 가능성이 있는 FraudModelService 같은 곳에만 적용).
 */
@Service
@RequiredArgsConstructor
public class FraudDetectionService {

    private final FraudDetectionResultRepository fraudDetectionResultRepository;
    private final FraudModelService fraudModelService;
    private final TransactionRepository transactionRepository; // C 담당 - JpaRepository 기본 메서드만 사용

    // 2차: 거래 하나를 AI 모델로 분석해서 결과 객체를 반환 (DB 저장은 3차에서)
    public FraudDetectionResponse detectFraud(FraudDetectionRequest request) {
        Long transactionId = request.getTransactionId();

        TransactionData transactionData = fetchTransactionData(transactionId);
        FraudPrediction prediction = fraudModelService.predict(transactionData);

        return FraudDetectionResponse.builder()
                .transactionId(transactionId)
                .fraudProbability(prediction.getFraudProbability())
                .predictedResult(prediction.getPredictedResult())
                .fraudType(prediction.getFraudType())
                .detectionReason(prediction.getDetectionReason())
                .userPatternScore(prediction.getUserPatternScore())
                .detectedAt(LocalDateTime.now())
                .build();
    }

    // 3차: 특정 탐지결과 ID로 단건 조회
    public FraudDetectionResponse getDetectionResult(Long detectionResultId) {
        throw new UnsupportedOperationException("3차에서 구현 예정");
    }

    // 3차: 특정 거래의 가장 최근 탐지결과 조회
    public FraudDetectionResponse getLatestByTransaction(Long transactionId) {
        throw new UnsupportedOperationException("3차에서 구현 예정");
    }

    /**
     * transactionId로 실제 거래정보를 조회해서 AI 입력용 TransactionData로 변환.
     *
     * 주의: TransactionRepository의 커스텀 메서드(findByIdAndUserId 등)는
     * 엔티티 필드명과 안 맞아서 지금 상태로는 쓸 수 없음(주석 처리됨).
     * 그래서 JpaRepository 기본 제공 메서드인 findById()만 사용하고,
     * 나머지 값들은 Transactions 엔티티의 연관관계에서 직접 꺼낸다.
     */
    private TransactionData fetchTransactionData(Long transactionId) {
        Transactions transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "존재하지 않는 거래입니다. transactionId=" + transactionId));

        // 신규 수취계좌 여부: TransferRecipients.isRegistered를 그대로 활용
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