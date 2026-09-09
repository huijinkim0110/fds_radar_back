package fds.radar.dto.dispute;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DisputeRequestResponse {
    private Long id;
    private Long userId; // [D파트 추가] 관리자 화면에 회원 구분 표시용
    private String userEmail; // [D파트 추가] 관리자 화면에 회원 이메일 표시용
    private Long transactionId; // [D파트 추가] 관리자 화면에 대상거래 표시용
    private String disputeType;
    private String requestReason; // [D파트 추가] 이의제기 상세 사유 표시용
    private Integer requestAmount; // [D파트 추가] 대상 거래 금액 표시용
    private String status;
    private String adminResponse; // [D파트 추가] 반려 사유 등 관리자 답변 표시용
    private LocalDateTime createdAt;
    private LocalDateTime processedAt; // [D파트 추가] 처리 완료 시각 표시용
}