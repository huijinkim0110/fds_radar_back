package fds.radar.dto.dispute;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DisputeRequest {
    private Long transactionId; 
    private String disputeType; 
    private String reason;
    private String detail;     
}//