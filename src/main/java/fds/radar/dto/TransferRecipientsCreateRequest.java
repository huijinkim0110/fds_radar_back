package fds.radar.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class TransferRecipientsCreateRequest {
    @NotBlank(message = "수취인 이름은 필수입니다.")
    @Size(max = 30, message = "수취인 이름은 최대 30자입니다.")
    private String recipientName;

    @NotNull(message = "금융기간은 필수입니다.")
    private Long institutionId;

    @NotBlank(message = "수취 계좌는 필수입니다.")
    private String accountNumber;
    
    public TransferRecipientsCreateRequest() {}

    public TransferRecipientsCreateRequest(String recipientName, Long institutionId, String accountNumber) {
        this.recipientName = recipientName;
        this.institutionId = institutionId;
        this.accountNumber = accountNumber;
    }

    public String getRecipientName() {return recipientName;}
    public Long getInstitutionId() {return institutionId;}
    public String getAccountNumber() {return accountNumber;}


}
