package fds.radar.dto.dispute;

import java.time.LocalDateTime;

import fds.radar.common.LockRequestStatus;
import fds.radar.common.RequestTargetType;
import fds.radar.entity.account.Accounts;
import fds.radar.entity.account.Cards;
import fds.radar.entity.dispute.LockRequests;
import fds.radar.entity.transaction.Transactions;

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

    // [D파트 담당자 추가] 잠금 대상이 어떤 유저의 무엇인지 화면에서 바로 보여주기 위한 필드
    // (기존엔 targetRefId만 있어서 "카드 #2"처럼 숫자만 보이고 누구 것인지 알 수 없었음)
    private Long ownerUserId;
    private String ownerName;
    private String targetDisplayNumber; // 마스킹된 카드번호 or 계좌번호

    public LockRequestResponse() {}

    public LockRequestResponse(Long id, RequestTargetType targetType, String requestReason,
                               LockRequestStatus requestStatus, Long fraudCaseId,
                               LocalDateTime requestedAt, LocalDateTime processedAt,
                               Boolean released, LocalDateTime releasedAt, Long targetRefId,
                               Long ownerUserId, String ownerName, String targetDisplayNumber) { // [D파트 담당자 수정] 파라미터 3개 추가
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
        // [D파트 담당자 추가]
        this.ownerUserId = ownerUserId;
        this.ownerName = ownerName;
        this.targetDisplayNumber = targetDisplayNumber;
    }

    public static LockRequestResponse from(LockRequests lock) {
        // [D파트 담당자 추가] 소유자/표시번호 계산을 위해 카드·계좌 엔티티를 먼저 조회
        Cards card = resolveCard(lock);
        Accounts account = resolveAccount(lock);

        Long ownerUserId = null;
        String ownerName = null;
        String targetDisplayNumber = null;

        if (card != null) {
            ownerUserId = card.getUser().getUserId();
            ownerName = card.getUser().getName();
            targetDisplayNumber = maskNumber(card.getCardNumber());
        } else if (account != null) {
            ownerUserId = account.getUser().getUserId();
            ownerName = account.getUser().getName();
            targetDisplayNumber = maskNumber(account.getAccountNumber());
        } else if (lock.getUser() != null) {
            // 카드/계좌 엔티티를 못 찾아도 최소한 요청한 유저 정보는 표시
            ownerUserId = lock.getUser().getUserId();
            ownerName = lock.getUser().getName();
        }

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
            resolveTargetRefId(lock), // [D파트 추가]
            ownerUserId, ownerName, targetDisplayNumber // [D파트 담당자 추가]
        );
    }

    // [D파트 추가] applyLock()/releaseLock()과 동일한 방식으로 실제 잠긴 대상(카드/계좌) ID를 구함
    private static Long resolveTargetRefId(LockRequests lock) {
        if (lock.getFraudCase() != null) {
            Transactions tx = lock.getFraudCase().getTransaction();
            if (lock.getTargetType() == RequestTargetType.CARD && tx.getCards() != null) {
                return tx.getCards().getCardId();
            } else if (lock.getTargetType() == RequestTargetType.ACCOUNT && tx.getAccount() != null) {
                return tx.getAccount().getAccountId();
            }
        }
        return lock.getTargetId();
    }

    // [D파트 담당자 추가] 카드 엔티티 자체를 구해서 소유자/카드번호까지 내려주기 위한 헬퍼
    private static Cards resolveCard(LockRequests lock) {
        if (lock.getTargetType() != RequestTargetType.CARD) return null;
        if (lock.getFraudCase() != null && lock.getFraudCase().getTransaction() != null) {
            return lock.getFraudCase().getTransaction().getCards();
        }
        return null;
    }

    // [D파트 담당자 추가] 계좌 엔티티를 구해서 소유자/계좌번호까지 내려주기 위한 헬퍼
    private static Accounts resolveAccount(LockRequests lock) {
        if (lock.getTargetType() != RequestTargetType.ACCOUNT) return null;
        if (lock.getFraudCase() != null && lock.getFraudCase().getTransaction() != null) {
            return lock.getFraudCase().getTransaction().getAccount();
        }
        return null;
    }

    // [D파트 담당자 추가] 카드번호/계좌번호 마스킹 (AccountResponse/CardResponse의 마스킹 방식과 동일)
    private static String maskNumber(String number) {
        if (number == null || number.length() < 4) return number;
        return "****" + number.substring(number.length() - 4);
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
    // [D파트 담당자 추가]
    public Long getOwnerUserId() {return ownerUserId;}
    public String getOwnerName() {return ownerName;}
    public String getTargetDisplayNumber() {return targetDisplayNumber;}
}