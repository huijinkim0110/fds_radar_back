package fds.radar.dto.account;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AccountCreateRequest {
   @Size(max = 30, message = "계좌 별칭은 최대 30자입니다.")
   private String accountName;

   @NotNull(message = "일일 이체한도는 필수입니다.")
   @DecimalMax(value = "0", message = "일일 이체한도는 0 이상이어야 합니다.")
   private BigDecimal dailyTransferLimit;

   public AccountCreateRequest() {}

   public AccountCreateRequest(String accountName, BigDecimal dailyTransferLimit) {
        this.accountName = accountName;
        this.dailyTransferLimit = dailyTransferLimit;
   }

    public String getAccountName() {
        return accountName;
    }   

    public BigDecimal getDailyTransferLimit() {
        return dailyTransferLimit;
    }
}
