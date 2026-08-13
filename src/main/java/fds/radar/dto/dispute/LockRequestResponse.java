package fds.radar.dto.dispute;

import java.time.LocalDateTime;

import fds.radar.common.LockRequestStatus;
import fds.radar.common.RequestTargetType;
import fds.radar.entity.dispute.LockRequests;

public class LockRequestResponse {
    
    private Long id;
    private RequestTargetType targetType;
    private String requestReason;
    private LockRequestStatus requestStatus;
    private Long fraudCaseId;
    private LocalDateTime requestedAt;
    private LocalDateTime processedAt;
    
    public LockRequestResponse() {}

    public LockRequestResponse(Long id, RequestTargetType targetType, String requestReason,
                               LockRequestStatus requestStatus, Long fraudCaseId,
                               LocalDateTime requestedAt, LocalDateTime processedAt) {
        this.id = id;
        this.targetType = targetType;
        this.requestReason = requestReason;
        this.requestStatus = requestStatus;
        this.fraudCaseId = fraudCaseId;
        this.requestedAt = requestedAt;
        this.processedAt = processedAt;
    }

    public static LockRequestResponse from(LockRequests lock) {
        return new LockRequestResponse(
            lock.getLockRequestId(),
            lock.getTargetType(),
            lock.getRequestReason(),
            lock.getRequestStatus(),
            lock.getFraudCase().getFraudCaseId(),
            lock.getRequestedAt(),
            lock.getProcessedAt()
        );
    }

    public Long getId() {return id;}
    public RequestTargetType getTargetType() {return targetType;}
    public String getRequestReason() {return requestReason;}
    public LockRequestStatus getRequestStatus() { return requestStatus; }
    public Long getFraudCaseId() {return fraudCaseId;}
    public LocalDateTime getRequestedAt() {return requestedAt;}
    public LocalDateTime getProcessedAt() {return processedAt;}
}
