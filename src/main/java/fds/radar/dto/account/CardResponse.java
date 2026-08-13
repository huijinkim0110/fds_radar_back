package fds.radar.dto.account;

import java.math.BigDecimal;

import fds.radar.common.CardStatus;
import fds.radar.common.CardType;
import fds.radar.entity.account.Cards;

public class CardResponse {

    private Long id;
    private Long institutionId;          // accountId → institutionId (카드는 기관 직속)
    private String cardName;
    private CardType cardType;
    private String cardNumber;
    private BigDecimal creditLimit;
    private BigDecimal availableLimit;
    private CardStatus status;

    public CardResponse() {}

    public CardResponse(Long id, Long institutionId, String cardName, CardType cardType,
                        String cardNumber, BigDecimal creditLimit,
                        BigDecimal availableLimit, CardStatus status) {
        this.id = id;
        this.institutionId = institutionId;
        this.cardName = cardName;
        this.cardType = cardType;
        this.cardNumber = cardNumber;
        this.creditLimit = creditLimit;
        this.availableLimit = availableLimit;
        this.status = status;
    }

    public static CardResponse from(Cards card) {
        return new CardResponse(
            card.getCardId(),
            card.getInstitution().getInstitutionId(),      // institution 타고 id
            card.getCardName(),
            card.getCardType(),
            maskCardNumber(card.getCardNumber()),
            card.getCreditLimit(),
            card.getAvailableLimit(),
            card.getStatus()
        );
    }

    // 카드번호 마스킹 (앞 4 + 뒤 4만, 가운데 가림)
    private static String maskCardNumber(String number) {
        if (number == null || number.length() < 8) return number;
        return number.substring(0, 4) + "****" + number.substring(number.length() - 4);
    }

    public Long getId() { return id; }
    public Long getInstitutionId() { return institutionId; }
    public String getCardName() { return cardName; }
    public CardType getCardType() { return cardType; }
    public String getCardNumber() { return cardNumber; }
    public BigDecimal getCreditLimit() { return creditLimit; }
    public BigDecimal getAvailableLimit() { return availableLimit; }
    public CardStatus getStatus() { return status; }
}