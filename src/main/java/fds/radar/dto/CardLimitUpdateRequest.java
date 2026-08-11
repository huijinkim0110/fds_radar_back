package fds.radar.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public class CardLimitUpdateRequest {
    
    @NotNull
    @DecimalMin(value = "0", inclusive = false, message = "이용한도는 0보다 커야 합니다.")
    private BigDecimal creditLimit;

    public CardLimitUpdateRequest() {}

    public CardLimitUpdateRequest(BigDecimal creditLimit) {
        this.creditLimit = creditLimit;
    }

    public BigDecimal getCreditLimit() {return creditLimit;}
}
