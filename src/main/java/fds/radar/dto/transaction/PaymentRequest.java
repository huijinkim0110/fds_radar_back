package fds.radar.dto.transaction;

import java.math.BigDecimal;

import fds.radar.common.TransactionChannel;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PaymentRequest {
    
    @NotNull(message = "카드는 필수입니다.")
    private Long cardId;

    @NotNull(message = "가맹점은 필수입니다.")
    private Long merchantId;

    @NotNull(message = "결제 금액은 필수입니다.")
    @DecimalMin(value = "0", inclusive = false, message = "금액은 0보다 커야합니다.")
    private BigDecimal amount;

    @NotNull(message = "결제 채널은 필수 입니다.")
    private TransactionChannel channel;

    private Long deviceId;

    @NotBlank(message = "멱등키는 필수입니다.")
    private String idempotencyKey;

    public PaymentRequest() {}

    public Long getCardId() {return cardId;}
    public Long getMerchantId() {return merchantId;}
    public BigDecimal getAmount() {return amount;}
    public TransactionChannel getChannel() {return channel;}
    public Long getDevice() {return deviceId;}
    public String getIdempotencyKey() {return idempotencyKey;}
}
