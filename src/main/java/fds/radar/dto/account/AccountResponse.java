package fds.radar.dto.account;

import java.math.BigDecimal;

import fds.radar.common.AccountStatus;
import fds.radar.entity.account.Accounts;

public class AccountResponse {
    
    private Long id;
    private String accountName;
    private String accountNumber;
    private BigDecimal balance;
    private BigDecimal dailyTransferLimit;
    private AccountStatus status;

    public AccountResponse() {}

    public AccountResponse(Long id, String accountName, String accountNumber,
                            BigDecimal balance, BigDecimal dailyTransferLimit,
                            AccountStatus status) {
        this.id = id;
        this.accountName = accountName;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.dailyTransferLimit = dailyTransferLimit;
        this.status = status;
    }

    public static AccountResponse from(Accounts account) {
        return new AccountResponse(
            account.getAccountId(),
            account.getAccountName(),
            maskAccountNumber(account.getAccountNumber()),
            account.getBalance(),
            account.getDailyTransferLimit(),
            account.getAccountStatus()
        );
    }

    private static String maskAccountNumber(String number) {
        if(number == null || number.length() < 4) return number;
        return "****" + number.substring(number.length() - 4);
    }

    public Long getId() {return id;}
    public String getAccountName() {return accountName;}
    public String getAccountNumber() {return accountNumber;}
    public BigDecimal getBalance() {return balance;}
    public BigDecimal getDailyTransferLimit() {return dailyTransferLimit;}
    public AccountStatus getStatus() {return status;}
}
