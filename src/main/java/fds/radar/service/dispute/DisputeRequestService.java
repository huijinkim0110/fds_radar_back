package fds.radar.service.dispute;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fds.radar.common.RequestStatus;
import fds.radar.dto.dispute.DisputeRequest;
import fds.radar.dto.dispute.DisputeRequestResponse;
import fds.radar.entity.dispute.DisputeRequests;
import fds.radar.entity.dispute.FraudReports;
import fds.radar.entity.user.Users;
import fds.radar.repository.dispute.DisputeRequestRepository;
import fds.radar.repository.fraud.FraudReportRepostitory;
import fds.radar.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DisputeRequestService {
    
    private final DisputeRequestRepository disputeRequestRepository;
    private final FraudReportRepostitory fraudReportRepostitory;
    private final UserRepository userRepository;

    // 이의제기 신청
    @Transactional
    public DisputeRequestResponse create(
            Long userId,
            DisputeRequest request) {

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> 
                    new IllegalArgumentException(
                            "사용자를 찾을 수 없습니다."
                    )
                );

        FraudReports fraudReport = fraudReportRepostitory
                .findById(request.getFraudReportId())
                .orElseThrow(() -> 
                    new IllegalArgumentException(
                            "신고 내역을 찾을 수 없습니다."
                    )
            );
        
        DisputeRequests disputeRequests = DisputeRequests.builder()
                .user(user)
                .transaction(fraudReport.getTransaction())
                .fraudReport(fraudReport)
                .disputeType(request.getDisputeType())
                .requestReason(request.getReason())
                .requestStatus(RequestStatus.RECEIVED)
                .requestedAt(LocalDateTime.now())
                .build();

            disputeRequestRepository.save(disputeRequests);

            return toResponse(disputeRequests);
    }

    // 사용자의 전체 이의제기 조회
    @Transactional(readOnly = true)
    public List<DisputeRequestResponse> getRequests(Long userId) {

        return disputeRequestRepository.findByUser_UserId(userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // 이의제기 한 건 조회
    @Transactional(readOnly = true)
    public DisputeRequestResponse getRequest(Long disputeRequestId) {

        DisputeRequests disputeRequest =
                disputeRequestRepository.findById(disputeRequestId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "이의제기 내역을 찾을 수 없습니다."
                                )
                        );

        return toResponse(disputeRequest);
    }

     // 관리자 승인
    @Transactional
    public DisputeRequestResponse approve(Long disputeRequestId) {

        DisputeRequests disputeRequest =
                disputeRequestRepository.findById(disputeRequestId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "이의제기 내역을 찾을 수 없습니다."
                                )
                        );

        disputeRequest.setRequestStatus(RequestStatus.APPROVED);
        disputeRequest.setProcessedAt(LocalDateTime.now());

        disputeRequestRepository.save(disputeRequest);

        return toResponse(disputeRequest);
    }

    // 관리자 이의제기 반려
    @Transactional
    public DisputeRequestResponse reject(
            Long disputeRequestId,
            String adminResponse) {

        DisputeRequests disputeRequest =
                disputeRequestRepository.findById(disputeRequestId)
                    .orElseThrow(() -> 
                        new IllegalArgumentException(
                                "이의제기 내역을 찾을 수 없습니다."
                        )
                );

        disputeRequest.setRequestStatus(RequestStatus.REJECTED);
        disputeRequest.setAdminResponse(adminResponse);
        disputeRequest.setProcessedAt(LocalDateTime.now());
        
        disputeRequestRepository.save(disputeRequest);
            
        return toResponse(disputeRequest);
    }

     // Entity -> Response 변환
    private DisputeRequestResponse toResponse(
            DisputeRequests disputeRequest) {

        return DisputeRequestResponse.builder()
                .id(disputeRequest.getDisputeRequestId())
                .disputeType(disputeRequest.getDisputeType())
                .status(disputeRequest.getRequestStatus().name())
                .createdAt(disputeRequest.getRequestedAt())
                .build();
    }
}
