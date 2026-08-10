package fds.radar.dto;

import java.math.BigDecimal;

import fds.radar.common.CardStatus;
import fds.radar.entity.account.Cards;

public class CardResponse {
 
    private Long id;
    private Long accountId;
    private String cardNumber;
    private BigDecimal creditLimit;
    private BigDecimal availableLimit;
    private CardStatus status;

    public CardResponse () {}

    public CardResponse(Long id, Long accountId, String cardNumber, BigDecimal creditLimit, 
                        BigDecimal availableLimit, CardStatus status) {
        this.id = id;
        this.accountId = accountId;
        this.cardNumber = cardNumber;
        this.creditLimit = creditLimit;
        this.availableLimit = availableLimit;
        this.status = status;
    }

    public static CardResponse from (Cards card) {
        return new CardResponse(
            card.getCardId(),
            card.getUser().getUserId(),
            maskCardNumber(card.getCardNumber()),
            card.getCreaditLimit(),
            card.getAvailableLimit(),
            card.getStatus()
        );
    }

     // 카드번호 마스킹 (앞 4 + 뒤 4만, 가운데 가림)
    private static String maskCardNumber(String number) {
        if (number == null || number.length() < 8) return number;
        return number.substring(0, 4) + "****" + number.substring(number.length() - 4);
    }

    public Long getId() {return id;}
    public Long getaccountId() {return accountId;}
    public String getCardNumber() {return cardNumber;}
    public BigDecimal getCreditLimit() {return creditLimit;}
    public BigDecimal getAvailableLimit() {return availableLimit;}
    public CardStatus getStatus() {return status;}
}
