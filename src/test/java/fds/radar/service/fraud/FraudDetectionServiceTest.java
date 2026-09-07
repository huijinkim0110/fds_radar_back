package fds.radar.service.fraud;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import fds.radar.dto.fraud.FraudDetectionRequest;
import fds.radar.dto.fraud.FraudDetectionResponse;

@SpringBootTest
class FraudDetectionServiceTest {

    @Autowired
    private FraudDetectionService fraudDetectionService;

    @Test
    void 이상거래_탐지_확인() {
        FraudDetectionRequest request = FraudDetectionRequest.builder()
                .transactionId(2L)
                .build();

        FraudDetectionResponse response = fraudDetectionService.detectFraud(request);

        System.out.println("=== 탐지 결과 ===");
        System.out.println("확률: " + response.getFraudProbability());
        System.out.println("판정: " + response.getPredictedResult());
        System.out.println("유형: " + response.getFraudType());
        System.out.println("사유: " + response.getDetectionReason());
    }
}