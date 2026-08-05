package fds.radar.entity.transaction;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transactions {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long accountId;

    @Column(nullable = false)
    private Long cardId;

    @Column(nullable = false)
    private Long recipientId;

    @Column(nullable = false)
    private Long merchantId;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;

    public enum TransactionType {
        CARD_PAYMNENT,
        ACCOUNT_TRANSFER
    }

    @Column(name = "amount", nullable = false)
    private Long amount; // 원 단위 거래 금액

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_channel", nullable = false)
    private TransactionChannel transactionChannel;

    public enum TransactionChannel {
        APP,
        WEB,
        ATM
    }
    
    @Column(nullable = false)
    private String countryCode;

    @Column(nullable = false)
    private String region;

    @Column(nullable = false)
    private Long deviceId;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_status", nullable = false)
    private String transactionStatus;

    public enum TransactionStatus {
        APPROVED,
        PENDING,
        CANCELED
    }

    @Column(nullable = false)
    private LocalDateTime occurredAt; // 거래 발생 시점

    @Column(nullable = false)
    private LocalDateTime approvedAt; // 거래 승인 시점
}
