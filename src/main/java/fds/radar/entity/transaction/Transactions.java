package fds.radar.entity.transaction;

import java.time.LocalDateTime;

import javax.smartcardio.Card;

import org.springframework.boot.security.autoconfigure.SecurityProperties.User;

import fds.radar.common.TransactionChannel;
import fds.radar.common.TransactionType;
import fds.radar.entity.UserDevices;
import fds.radar.entity.account.Accounts;
import fds.radar.entity.account.TransferRecipients;
import fds.radar.entity.user.UserDevices;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = true)
    private Accounts account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id", nullable = true)
    private Card card;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = true)
    private TransferRecipients recipient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id", nullable = true)
    private Merchants merchant;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType transactionType;

    @Column(name = "amount", nullable = false)
    private Long amount; // 원 단위 거래 금액

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionChannel transactionChannel;

    @Column(nullable = false)
    private String countryCode;

    @Column(nullable = false)
    private String region;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = true) // 기기 정보 수집이 필수라면 nullable = false
    private UserDevices device;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private String transactionStatus;

    @Column(nullable = false)
    private LocalDateTime occurredAt; // 거래 발생 시점

    @Column(nullable = false)
    private LocalDateTime approvedAt; // 거래 승인 시점
}
