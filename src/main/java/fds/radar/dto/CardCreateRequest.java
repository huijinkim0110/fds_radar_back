package fds.radar.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public class CardCreateRequest {
    
    @NotNull(message = "연결된 계좌 ID는 필수입니다.")
    private Long accountId;

    @NotNull(message = "이용한도는 필수입니다.")
    @DecimalMin(value = "0", inclusive = false, message = "아용한도는 0보다 커야 합니다.")
    private BigDecimal creditLimit; // 0 이상

    public CardCreateRequest() {}

    public CardCreateRequest(Long accountId, BigDecimal creditLimit) {
        this.accountId = accountId;
        this.creditLimit = creditLimit;
    }

    public Long getAccountId() {return accountId;}
    public BigDecimal getCreditLimit() {return creditLimit;}
}
