package fds.radar.controller.fraud;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fds.radar.dto.fraud.FraudCaseAssignRequest;
import fds.radar.dto.fraud.FraudCaseDetailResponse;
import fds.radar.dto.fraud.FraudCaseHistoryResponse;
import fds.radar.dto.fraud.FraudCaseListResponse;
import fds.radar.dto.fraud.FraudCaseStatusRequest;
import fds.radar.dto.fraud.FraudDecisionRequest;
import fds.radar.service.fraud.FraudCaseHistoryService;
import fds.radar.service.fraud.FraudCaseService;
import lombok.RequiredArgsConstructor;

/**
 * 관리자 전용 이상거래 사건 처리 API.
 * 사용자용 API(FraudCaseController)와 경로를 분리해서
 * 나중에 관리자 권한 체크(Security)를 이 컨트롤러 단위로 적용하기 쉽게 함.
 */
@RestController
@RequestMapping("/api/admin/fraud-cases")
@RequiredArgsConstructor
public class FraudCaseAdminController {

    private final FraudCaseService fraudCaseService;
    private final FraudCaseHistoryService fraudCaseHistoryService;

    // 5차: 관리자 사건 목록 조회
    @GetMapping
    public ResponseEntity<List<FraudCaseListResponse>> getCaseList() {
        return ResponseEntity.ok(fraudCaseService.getCaseList());
    }

    // 5차: 관리자 사건 상세 조회
    @GetMapping("/{fraudCaseId}")
    public ResponseEntity<FraudCaseDetailResponse> getCaseDetail(@PathVariable Long fraudCaseId) {
        return ResponseEntity.ok(fraudCaseService.getCaseDetail(fraudCaseId));
    }

    // 6차: 사건 상태 변경
    @PatchMapping("/{fraudCaseId}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable Long fraudCaseId,
                                              @RequestBody FraudCaseStatusRequest request) {
        fraudCaseService.updateCaseStatus(fraudCaseId, request);
        return ResponseEntity.ok().build();
    }

    // 6차: 담당 관리자 배정
    @PatchMapping("/{fraudCaseId}/assignee")
    public ResponseEntity<Void> assignAdmin(@PathVariable Long fraudCaseId,
                                             @RequestBody FraudCaseAssignRequest request) {
        fraudCaseService.assignAdmin(fraudCaseId, request);
        return ResponseEntity.ok().build();
    }

    // 9차: 최종 판정(정상/사기)
    @PatchMapping("/{fraudCaseId}/decision")
    public ResponseEntity<Void> finalizeDecision(@PathVariable Long fraudCaseId,
                                                  @RequestBody FraudDecisionRequest request) {
        fraudCaseService.finalizeDecision(fraudCaseId, request);
        return ResponseEntity.ok().build();
    }

    // 7차: 사건 처리이력 시간순 조회
    @GetMapping("/{fraudCaseId}/histories")
    public ResponseEntity<List<FraudCaseHistoryResponse>> getHistories(@PathVariable Long fraudCaseId) {
        return ResponseEntity.ok(fraudCaseHistoryService.getHistories(fraudCaseId));
    }
}