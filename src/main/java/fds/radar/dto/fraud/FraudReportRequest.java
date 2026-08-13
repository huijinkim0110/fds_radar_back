package fds.radar.dto.fraud;

import lombok.Getter;

@Getter
public class FraudReportRequest {
    private Long transactionId;
    private String reason;
}
