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
import fds.radar.entity.transaction.Transactions; // 추가
import fds.radar.entity.user.Users;
import fds.radar.repository.dispute.DisputeRequestRepository;
import fds.radar.repository.transaction.TransactionRepository; // 추가
import fds.radar.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DisputeRequestService {
    
    private final DisputeRequestRepository disputeRequestRepository;
    private final TransactionRepository transactionRepository; // 1. TransactionRepository 추가
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

        // 2. FraudReport 대신 Transaction 조회
        Transactions transaction = transactionRepository.findById(request.getTransactionId())
                .orElseThrow(() -> 
                    new IllegalArgumentException(
                            "거래 내역을 찾을 수 없습니다."
                    )
                );
        
        // 3. DisputeRequests 엔티티 생성 시 조회한 Transaction 설정
        DisputeRequests disputeRequests = DisputeRequests.builder()
                .user(user)
                .transaction(transaction)
                .disputeType(request.getDisputeType()) // [D파트 수정] request.getReason() 대신 실제 유형 필드로 수정 — 기존엔 유형 값이 저장 안 되고 사유로 덮어써지던 버그
                .requestReason(request.getReason())
                .requestAmount(transaction.getAmount().intValue()) // [D파트 추가] 관리자 화면 표시용, 대상 거래 금액 자동 반영
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

    // [D파트 추가] 관리자용 전체 이의제기 목록 조회 — 신청일시 최신순
    @Transactional(readOnly = true)
    public List<DisputeRequestResponse> getAllRequests() {
        return disputeRequestRepository.findAll()
                .stream()
                .sorted((a, b) -> b.getRequestedAt().compareTo(a.getRequestedAt()))
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
                .userId(disputeRequest.getUser().getUserId()) // [D파트 추가]
                .userEmail(disputeRequest.getUser().getEmail()) // [D파트 추가]
                .transactionId(disputeRequest.getTransaction().getTransactionId()) // [D파트 추가]
                .disputeType(disputeRequest.getDisputeType())
                .requestReason(disputeRequest.getRequestReason()) // [D파트 추가]
                .requestAmount(disputeRequest.getRequestAmount()) // [D파트 추가]
                .status(disputeRequest.getRequestStatus().name())
                .adminResponse(disputeRequest.getAdminResponse()) // [D파트 추가]
                .createdAt(disputeRequest.getRequestedAt())
                .processedAt(disputeRequest.getProcessedAt()) // [D파트 추가]
                .build();
    }
}