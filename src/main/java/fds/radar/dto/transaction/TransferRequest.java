package fds.radar.dto.transaction;

import java.math.BigDecimal;

import fds.radar.common.TransactionChannel;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TransferRequest {
    
    @NotNull(message = "출금 계좌는 필수입니다.")
    private Long fromAccountId;

    @NotBlank(message = "받는 계좌번호는 필수입니다.")
    private String receiverAccountNumber;

    @NotNull(message = "이체 금액은 필수입니다")
    @DecimalMin(value = "0", inclusive = false, message = "금액은 0보다 커야 합니다")
    private BigDecimal amount;             

    @NotNull(message = "이체 채널은 필수입니다")
    private TransactionChannel channel;     

    private Long deviceId;                 

    @NotBlank
    private String idempotencyKey;          // 중복이체 방지

    public TransferRequest() {}

    public Long getFromAccountId() { return fromAccountId; }
    public String getReceiverAccountNumber() { return receiverAccountNumber; }
    public BigDecimal getAmount() { return amount; }
    public TransactionChannel getChannel() { return channel; }
    public Long getDeviceId() { return deviceId; }
    public String getIdempotencyKey() { return idempotencyKey; }

}
