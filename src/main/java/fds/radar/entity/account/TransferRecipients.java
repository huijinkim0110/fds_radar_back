package fds.radar.entity.account;

import java.time.LocalDateTime;

import fds.radar.entity.user.Users;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
public class TransferRecipients {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recipientId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    // 수취인 이름
    @Column(nullable = false)
    private String recipientName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_id", nullable = false)
    private Institutions institution;

    // 수취 계좌번호
    @Column(nullable = false)
    private String accountNumber;

    @Column(name = "is_registered", nullable = false)
    @Builder.Default
    private boolean isRegistered = false;

    public boolean isRegistered() {
        return isRegistered;
    }

    public void setRegistered(boolean registered) {
        isRegistered = registered;
    }

    // 최초 송금 시점
    @Column(nullable = false)
    private LocalDateTime firstTransferAt;

    // 최근 송금 시점
    @Column(nullable = false)
    private LocalDateTime lastTransferAt;

}
