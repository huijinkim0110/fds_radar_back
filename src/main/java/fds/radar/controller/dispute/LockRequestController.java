// LockRequestController
package fds.radar.controller.dispute;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import fds.radar.dto.dispute.LockRequestCreateRequest;
import fds.radar.dto.dispute.LockRequestProcessRequest;
import fds.radar.dto.dispute.LockRequestResponse;
import fds.radar.service.dispute.LockRequestService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/locks")
@CrossOrigin(origins = "*")
public class LockRequestController {

    private final LockRequestService lockRequestService;

    public LockRequestController(LockRequestService lockRequestService) {
        this.lockRequestService = lockRequestService;
    }

    // 유저 → 관리자 잠금/해제 요청
    @PostMapping
    public ResponseEntity<LockRequestResponse> requestByUser(
            @RequestParam Long userId,
            @Valid @RequestBody LockRequestCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(lockRequestService.requestByUser(userId, request));
    }

    // 내 잠금 요청 목록
    @GetMapping
    public ResponseEntity<List<LockRequestResponse>> getMyLockRequests(
            @RequestParam Long userId) {
        return ResponseEntity.ok(lockRequestService.getMyLockRequests(userId));
    }

    // 잠금 요청 상세
    @GetMapping("/{lockId}")
    public ResponseEntity<LockRequestResponse> getLockRequest(
            @RequestParam Long userId,
            @PathVariable Long lockId) {
        return ResponseEntity.ok(lockRequestService.getLockRequest(userId, lockId));
    }

    // 관리자 처리 대기 목록
    @GetMapping("/admin/pending")
    public ResponseEntity<List<LockRequestResponse>> getReceivedRequests() {
        return ResponseEntity.ok(lockRequestService.getReceivedRequests());
    }

    // 관리자 승인/반려
    @PatchMapping("/admin/{lockId}")
    public ResponseEntity<LockRequestResponse> process(
            @PathVariable Long lockId,
            @Valid @RequestBody LockRequestProcessRequest request) {
        return ResponseEntity.ok(lockRequestService.process(lockId, request));
    }

    // fraud_case 기반 자동잠금 (D 연동)
    @PostMapping("/from-fraud-case")
    public ResponseEntity<LockRequestResponse> createFromFraudCase(
            @Valid @RequestBody LockRequestCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(lockRequestService.createFromFraudCase(request));
    }
}