package fds.radar.dto.account;

import java.time.LocalDateTime;

import fds.radar.entity.account.TransferRecipients;

public class TransferRecipientResponse {
    
   private Long id;
   private String recipientName;
   private Long institutionId;
   private String institutionName;
   private String accountNumber;
   private boolean isNewRecipient;
   private LocalDateTime lastTransferAt;

    public TransferRecipientResponse () {}

    public TransferRecipientResponse(Long id, String recipientName,
        Long institutionId, String institutionName, String accountNumber, boolean isNewRecipient, LocalDateTime lastTransferAt
    ) {
        this.id = id;
        this.recipientName = recipientName;
        this.institutionId = institutionId;
        this.institutionName = institutionName;
        this.accountNumber = accountNumber;
        this.isNewRecipient = isNewRecipient;
        this.lastTransferAt = lastTransferAt;
    }

    public static TransferRecipientResponse from (TransferRecipients recipients) {
        return new TransferRecipientResponse(
            recipients.getRecipientId(),
            recipients.getRecipientName(),
            recipients.getInstitution().getInstitutionId(),
            recipients.getInstitution().getInstitutionName(),
            maskAccountNumber(recipients.getAccountNumber()),
            recipients.getFirstTransferAt() == null, // 이체 이력 없으면 신규 
            recipients.getLastTransferAt()
        );
    }

    private static String maskAccountNumber(String number) {
        if(number == null || number.length() < 4) return number;
        return "****" + number.substring(number.length() - 4);
    }

    public Long getId() {return id;}
    public String getRecipientName () {return recipientName;}
    public Long getInstitutionId() {return institutionId;}
    public String getInstitutionName() {return institutionName;}
    public String getAccountNumber() {return accountNumber;}
    public boolean isNewRecipient() {return isNewRecipient;}
    public LocalDateTime getLastTransferAt() {return lastTransferAt;}
    
}
