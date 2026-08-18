package fds.radar.dto.account;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TransferRequest {
    
    private Long cardId; // 카드 송금 일 때만

    @NotBlank(message = "받는 계좌번호는 필수입니다.")
    private String receiverAccountNumber; // 상대방 계좌번호

    @NotNull(message = "송금 금액은 필수입니다.")
    @Min(value = 1, message = "송금 금액은 1원 이상이어야합니다.")
    private Long amount;

    public TransferRequest() {}

    public TransferRequest(Long cardId, String receiverAccountNumber, Long amount) {
        this.cardId = cardId;
        this.receiverAccountNumber = receiverAccountNumber;
        this.amount = amount;
    }

    public Long getCardId() {
        return cardId;
    }

    public String getReceiverAccountNumber() {
        return receiverAccountNumber;
    }

    public Long getAmount() {
        return amount;
    }


}
