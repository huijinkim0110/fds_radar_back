package fds.radar.dto.fraud;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FraudReportResponse {
    private Long id;
    private Long userId; // [D파트 추가] 관리자 화면에 회원 구분 표시용
    private String userEmail; // [D파트 추가] 관리자 화면에 회원 이메일 표시용
    private Long transactionId;
    private String transactionType; // [D파트 추가] 관리자 화면에 거래유형(카드결제/계좌이체) 표시용
    private String reportTypeLabel; // [D파트 추가] ReportType enum 한글 라벨(피해 신고/사후 신고)
    private String reason;
    private String status;
    private String statusLabel; // [D파트 추가] ReportStatus enum 한글 라벨(접수/검토중/처리 완료)
    private LocalDateTime createdAt;
    private LocalDateTime processedAt; // [D파트 추가] 관리자 처리 완료 시각 표시용
}