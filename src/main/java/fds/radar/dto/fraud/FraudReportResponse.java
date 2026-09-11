package fds.radar.dto.fraud;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FraudReportResponse {
    private Long id;
    private Long userId;
    private String userName;  // ← 추가
    private String userEmail;
    private Long transactionId;
    private String transactionType;
    private String reportTypeLabel;
    private String reasonCategory;
    private String reason;
    private String status;
    private String statusLabel;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
}