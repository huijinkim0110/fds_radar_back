package fds.radar.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public class AccountLimitUpdateRequest {
    
    @NotNull
    @DecimalMin(value = "0", message = "일일 이체한도는 0 이상이어야 합니다.")
    private BigDecimal dailyTransferLimit;

    public AccountLimitUpdateRequest() {}

    public AccountLimitUpdateRequest(BigDecimal dailyTransferLimit) {
        this.dailyTransferLimit = dailyTransferLimit;
    }

    public BigDecimal getDailyTransferLimit() {
        return dailyTransferLimit;
    }
}
