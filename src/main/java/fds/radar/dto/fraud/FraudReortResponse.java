package fds.radar.dto.fraud;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FraudReortResponse {
    private Long id;
    private Long transactionId;
    private String reason;
    private String status;
    private LocalDateTime createdAt;
}
