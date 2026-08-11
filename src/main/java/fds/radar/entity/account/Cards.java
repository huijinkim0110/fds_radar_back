package fds.radar.entity.account;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import fds.radar.common.CardStatus;
import fds.radar.common.CardType;
import fds.radar.entity.user.Users;
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
public class Cards {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cardId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_id", nullable = false)
    private Institutions institution;

    // 카드 번소 -> UI 표시할 때 마스킹 패턴 표시
    @Column(nullable = false)
    private String cardNumber; 

    @Column(nullable = false)
    private String cardName;

    @Enumerated(EnumType.STRING)
    @Column(name = "card_type", nullable = false)
    private CardType cardType;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal creditLimit;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal availableLimit;

    @Enumerated(EnumType.STRING)
    @Column(name = "card_status", nullable = false)
    @Builder.Default
    private CardStatus status = CardStatus.ACTIVE;

    // 카드 발급 시점
    @Column(nullable = false)
    private LocalDateTime issuedAt;

    // 카드 만료 시점
    @Column(nullable = false)
    private LocalDateTime expiredAt;
}

