package fds.radar.dto.dispute;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DisputeRequestResponse {
    private Long id;
    private String disputeType;
    private String status;
    private LocalDateTime createdAt;
}
