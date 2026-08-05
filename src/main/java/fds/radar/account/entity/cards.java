package fds.radar.account.entity;

import java.time.LocalDateTime;

import fds.radar.account.common.CardStatus;
import fds.radar.account.common.CardType;
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
public class Cards {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cardId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long institutionId;

    // 카드 번소 -> UI 표시할 때 마스킹 패턴 표시
    @Column(nullable = false)
    private String cardNumber; 

    @Column(nullable = false)
    private String cardName;

    @Enumerated(EnumType.STRING)
    @Column(name = "card_type", nullable = false)
    private CardType cardType;

    @Column(nullable = false)
    private Long creaditLimit;

    @Column(nullable = false)
    private Long availableLimit;

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

