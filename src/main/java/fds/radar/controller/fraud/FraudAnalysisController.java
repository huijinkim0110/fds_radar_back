package fds.radar.controller.fraud;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fds.radar.dto.fraud.FraudCaseListResponse;
import fds.radar.service.fraud.FraudAnalysisService;
import lombok.RequiredArgsConstructor;

/**
 * 관리자 전용 오탐·미탐 조회 API.
 * 사건 처리(FraudCaseAdminController)와 성격이 달라서 컨트롤러도 분리.
 */
@RestController
@RequestMapping("/api/admin/fraud-analysis")
@RequiredArgsConstructor
public class FraudAnalysisController {

    private final FraudAnalysisService fraudAnalysisService;

    // 7차: 오탐 목록 조회
    @GetMapping("/false-positives")
    public ResponseEntity<List<FraudCaseListResponse>> getFalsePositives() {
        return ResponseEntity.ok(fraudAnalysisService.getFalsePositives());
    }

    // 7차: 미탐 목록 조회
    @GetMapping("/false-negatives")
    public ResponseEntity<List<FraudCaseListResponse>> getFalseNegatives() {
        return ResponseEntity.ok(fraudAnalysisService.getFalseNegatives());
    }
}