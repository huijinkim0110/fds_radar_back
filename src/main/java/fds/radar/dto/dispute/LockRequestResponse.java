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
            // [D파트 담당자 수정] 유저가 직접 요청한 잠금(requestByUser)은 fraudCase가 null이라 NPE 발생 → null 체크 추가
            lock.getFraudCase() != null ? lock.getFraudCase().getFraudCaseId() : null,
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
