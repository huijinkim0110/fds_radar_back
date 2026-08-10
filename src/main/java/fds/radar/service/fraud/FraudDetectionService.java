package fds.radar.service.fraud;

import org.springframework.stereotype.Service;

import fds.radar.dto.fraud.FraudDetectionRequest;
import fds.radar.dto.fraud.FraudDetectionResponse;
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

    // 2~3차: 거래 하나를 AI 모델로 분석해서 fraud_detection_results에 저장 후 반환
    public FraudDetectionResponse detectFraud(FraudDetectionRequest request) {
        throw new UnsupportedOperationException("2차에서 구현 예정");
    }

    // 3차: 특정 탐지결과 ID로 단건 조회
    public FraudDetectionResponse getDetectionResult(Long detectionResultId) {
        throw new UnsupportedOperationException("3차에서 구현 예정");
    }

    // 3차: 특정 거래의 가장 최근 탐지결과 조회
    public FraudDetectionResponse getLatestByTransaction(Long transactionId) {
        throw new UnsupportedOperationException("3차에서 구현 예정");
    }
}