package fds.radar.dto.transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import fds.radar.common.TransactionChannel;
import fds.radar.common.TransactionStatus;
import fds.radar.common.TransactionType;
import fds.radar.entity.transaction.Transactions;

public class TransactionResponse {

    private Long transactionId;
    private TransactionType transactionType;
    private BigDecimal amount;
    private TransactionChannel channel;
    private TransactionStatus status;
    private LocalDateTime occurredAt;
    private LocalDateTime approvedAt;
    private String merchantName;   // 카드결제일 때만 값 있음
    private String recipientName;  // 계좌이체일 때만 값 있음

    public TransactionResponse() {}

    public TransactionResponse(Long transactionId, TransactionType transactionType, BigDecimal amount,
                               TransactionChannel channel, TransactionStatus status,
                               LocalDateTime occurredAt, LocalDateTime approvedAt,
                               String merchantName, String recipientName) {
        this.transactionId = transactionId;
        this.transactionType = transactionType;
        this.amount = amount;
        this.channel = channel;
        this.status = status;
        this.occurredAt = occurredAt;
        this.approvedAt = approvedAt;
        this.merchantName = merchantName;
        this.recipientName = recipientName;
    }

    public static TransactionResponse from(Transactions tx) {
        return new TransactionResponse(
            tx.getTransactionId(),
            tx.getTransactionType(),
            tx.getAmount(),
            tx.getTransactionChannel(),
            tx.getTransactionStatus(),
            tx.getOccurredAt(),
            tx.getApprovedAt(),
            tx.getMerchant() != null ? tx.getMerchant().getMerchantName() : null,
            tx.getRecipient() != null ? tx.getRecipient().getRecipientName() : null
        );
    }

    public Long getTransactionId() { return transactionId; } // [D파트 담당자 수정] 대소문자 수정
    public TransactionType getTransactionType() { return transactionType; }
    public BigDecimal getAmount() { return amount; }
    public TransactionChannel getChannel() { return channel; }
    public TransactionStatus getStatus() { return status; }
    public LocalDateTime getOccurredAt() { return occurredAt; }
    public LocalDateTime getApprovedAt() { return approvedAt; }
    public String getMerchantName() { return merchantName; }
    public String getRecipientName() { return recipientName; }
}