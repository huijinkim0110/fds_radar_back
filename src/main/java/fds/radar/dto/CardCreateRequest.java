package fds.radar.dto;

import java.math.BigDecimal;

import fds.radar.common.CardType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CardCreateRequest {

    @NotNull(message = "발급 금융기관은 필수입니다")
    private Long institutionId;              // ✅ 어느 기관에서 발급

    @NotBlank(message = "카드 별칭은 필수입니다")
    @Size(max = 30, message = "카드 별칭은 최대 30자입니다")
    private String cardName;                 // ✅ 유저 입력

    @NotNull(message = "카드 종류는 필수입니다")
    private CardType cardType;               // ✅ 유저 선택

    @NotNull(message = "이용한도는 필수입니다")
    @DecimalMin(value = "0", inclusive = false, message = "이용한도는 0보다 커야 합니다")
    private BigDecimal creditLimit;          // ✅ 0 초과

    public CardCreateRequest() {}

    public CardCreateRequest(Long institutionId, String cardName, CardType cardType,
                            BigDecimal creditLimit) {
        this.institutionId = institutionId;
        this.cardName = cardName;
        this.cardType = cardType;
        this.creditLimit = creditLimit;
    }

    public Long getInstitutionId() { return institutionId; }
    public String getCardName() { return cardName; }
    public CardType getCardType() { return cardType; }
    public BigDecimal getCreditLimit() { return creditLimit; }
}