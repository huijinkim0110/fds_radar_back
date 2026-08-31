package fds.radar.controller.fraud;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fds.radar.dto.fraud.FraudDetectionRequest;
import fds.radar.dto.fraud.FraudDetectionResponse;
import fds.radar.service.fraud.FraudDetectionService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/fraud-detections")
@RequiredArgsConstructor
public class FraudDetectionController {

    private final FraudDetectionService fraudDetectionService;

    // 2~3차: 거래 하나를 AI 모델로 분석 요청
    @PostMapping
    public ResponseEntity<FraudDetectionResponse> detectFraud(@RequestBody FraudDetectionRequest request) {
        return ResponseEntity.ok(fraudDetectionService.detectFraud(request));
    }

    // 3차: 탐지결과 ID로 단건 조회
    @GetMapping("/{detectionResultId}")
    public ResponseEntity<FraudDetectionResponse> getDetectionResult(@PathVariable Long detectionResultId) {
        return ResponseEntity.ok(fraudDetectionService.getDetectionResult(detectionResultId));
    }

    // 3차: 특정 거래의 최신 탐지결과 조회
    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<FraudDetectionResponse> getLatestByTransaction(@PathVariable Long transactionId) {
        return ResponseEntity.ok(fraudDetectionService.getLatestByTransaction(transactionId));
    }
}