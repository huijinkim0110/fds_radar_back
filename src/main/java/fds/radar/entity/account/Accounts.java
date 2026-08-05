package fds.radar.entity.account;

import java.time.LocalDateTime;

import fds.radar.common.AccountStatus;
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
public class Accounts {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountId;

    @Column(nullable = false)    
    private Long userId;

    @Column(nullable = false)
    private Long institutionId;

    // 계좌번호 (후에 자동 생성 로직으로 변경)
    @Column(unique = true, nullable = false)
    private String accountNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false)
    private AccountType accountType;

    public enum AccountType {
        CHECKING,
        DEPOSIT,
        SAVINGS;
    }

    @Column(nullable = false)
    private Long balance;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_status", nullable = false)
    @Builder.Default
    private AccountStatus accountStatus = AccountStatus.ACTIVE;

    @Column(nullable = false)
    private Long dailyTransferLimit;

    @Column(nullable = false)
    private LocalDateTime openedAt;

    
}
