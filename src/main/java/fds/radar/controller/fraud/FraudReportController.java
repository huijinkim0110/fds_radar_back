package fds.radar.controller.fraud;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fds.radar.common.ReportStatus;
import fds.radar.dto.fraud.FraudReportRequest;
import fds.radar.dto.fraud.FraudReportResponse;
import fds.radar.service.fraud.FraudReportService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("/api/fraud-reports")
@RequiredArgsConstructor
public class FraudReportController {
    
    private final FraudReportService fraudReportService;

    // 피해 의심 거래 신고 접수
    @PostMapping("/users/{userId}")
    public ResponseEntity<FraudReportResponse> create(
            @PathVariable Long userId,
            @RequestBody FraudReportRequest request) {

        FraudReportResponse response = 
                fraudReportService.create(userId, request);

        return ResponseEntity.ok(response);
    }
    
    // 사용자의 신고 목록 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<FraudReportResponse>> getReports(
            @PathVariable Long userId) {

        List<FraudReportResponse> responses = 
                fraudReportService.getReports(userId);
        
        return ResponseEntity.ok(responses);
    }
    
    // 신고 한 건 조회
    @GetMapping("/{reportId}")
    public ResponseEntity<FraudReportResponse> getReport(
            @PathVariable Long reportId) {

        FraudReportResponse response = 
                fraudReportService.getReport(reportId);

        return ResponseEntity.ok(response);
    }
    
    // 관리자 신고 처리 상태 변경
    @PatchMapping("/{reportId}/status")
    public ResponseEntity<FraudReportResponse> updateStatus(
            @PathVariable Long reportId,
            @RequestParam ReportStatus status) {

        FraudReportResponse response =
                fraudReportService.updateStatus(reportId, status);

        return ResponseEntity.ok(response);
    }
}
