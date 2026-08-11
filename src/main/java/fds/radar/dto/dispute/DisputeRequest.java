package fds.radar.dto.dispute;

import lombok.Getter;

@Getter
public class DisputeRequest {
    private Long fraudReportId;
    private String disputeType;
    private String reason;
}
