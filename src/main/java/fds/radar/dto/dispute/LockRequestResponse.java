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
    private Boolean released;
    private LocalDateTime releasedAt;
    private Long targetRefId; // [D파트 추가] 실제 잠긴 카드/계좌의 ID — 화면에 "무엇을" 잠갔는지 표시용

    public LockRequestResponse() {}

    public LockRequestResponse(Long id, RequestTargetType targetType, String requestReason,
                               LockRequestStatus requestStatus, Long fraudCaseId,
                               LocalDateTime requestedAt, LocalDateTime processedAt,
                               Boolean released, LocalDateTime releasedAt, Long targetRefId) { // [D파트 수정]
        this.id = id;
        this.targetType = targetType;
        this.requestReason = requestReason;
        this.requestStatus = requestStatus;
        this.fraudCaseId = fraudCaseId;
        this.requestedAt = requestedAt;
        this.processedAt = processedAt;
        this.released = released;
        this.releasedAt = releasedAt;
        this.targetRefId = targetRefId; // [D파트 추가]
    }

    public static LockRequestResponse from(LockRequests lock) {
        return new LockRequestResponse(
            lock.getLockRequestId(),
            lock.getTargetType(),
            lock.getRequestReason(),
            lock.getRequestStatus(),
            lock.getFraudCase() != null ? lock.getFraudCase().getFraudCaseId() : null,
            lock.getRequestedAt(),
            lock.getProcessedAt(),
            lock.getReleased(),
            lock.getReleasedAt(),
            resolveTargetRefId(lock) // [D파트 추가]
        );
    }

    // [D파트 추가] applyLock()/releaseLock()과 동일한 방식으로 실제 잠긴 대상(카드/계좌) ID를 구함
    private static Long resolveTargetRefId(LockRequests lock) {
        if (lock.getFraudCase() != null) {
            if (lock.getTargetType() == RequestTargetType.CARD) {
                return lock.getFraudCase().getTransaction().getCards().getCardId();
            } else if (lock.getTargetType() == RequestTargetType.ACCOUNT) {
                return lock.getFraudCase().getTransaction().getAccount().getAccountId();
            }
        }
        return lock.getTargetId();
    }

    public Long getId() {return id;}
    public RequestTargetType getTargetType() {return targetType;}
    public String getRequestReason() {return requestReason;}
    public LockRequestStatus getRequestStatus() { return requestStatus; }
    public Long getFraudCaseId() {return fraudCaseId;}
    public LocalDateTime getRequestedAt() {return requestedAt;}
    public LocalDateTime getProcessedAt() {return processedAt;}
    public Boolean getReleased() {return released;}
    public LocalDateTime getReleasedAt() {return releasedAt;}
    public Long getTargetRefId() {return targetRefId;} // [D파트 추가]
}