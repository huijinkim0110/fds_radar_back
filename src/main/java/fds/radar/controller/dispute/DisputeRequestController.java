package fds.radar.controller.dispute;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fds.radar.dto.dispute.DisputeRequest;
import fds.radar.dto.dispute.DisputeRequestResponse;
import fds.radar.service.dispute.DisputeRequestService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;





@RestController
@RequestMapping("/api/dispute-requests")
@RequiredArgsConstructor
public class DisputeRequestController {
    
    private final DisputeRequestService disputeRequestService;
    
    // 이의제기 신청
    @PostMapping("/users/{userId}")
    public ResponseEntity<DisputeRequestResponse> create(
            @PathVariable Long userId,
            @RequestBody DisputeRequest request) {

        DisputeRequestResponse response =
                disputeRequestService.create(userId, request);
            
        return ResponseEntity.ok(response);

        }

        // 사용자의 전체 이의제기 조회
        @GetMapping("/user/{userId}")
        public ResponseEntity<List<DisputeRequestResponse>> getRequests(
                @PathVariable Long userId) {

            List<DisputeRequestResponse> responses = 
                    disputeRequestService.getRequests(userId);

            return ResponseEntity.ok(responses);
               
        }
        

        // 이의제기 한 건 조회
        @GetMapping("/{disputeRequestId}")
        public ResponseEntity<DisputeRequestResponse> getRequest(
                @PathVariable Long disputeRequestId) {

            DisputeRequestResponse response =
                    disputeRequestService.getRequest(disputeRequestId);
            
            return ResponseEntity.ok(response);
        }
    
    // 관리자 이의제기 승인
    @PutMapping("/{disputeRequestId}/approve")
    public ResponseEntity<DisputeRequestResponse> approve(
            @PathVariable Long disputeRequestId) {

        DisputeRequestResponse response = 
                disputeRequestService.approve(disputeRequestId);

        return ResponseEntity.ok(response);
    }

    // 관리자 이의제기 반려
    @PutMapping("/{disputeRequestId}/reject")
public ResponseEntity<DisputeRequestResponse> reject(
        @PathVariable Long disputeRequestId,
        @RequestParam String adminResponse) {

    DisputeRequestResponse response =
            disputeRequestService.reject(
                    disputeRequestId,
                    adminResponse
            );

    return ResponseEntity.ok(response);
    
    }
}
