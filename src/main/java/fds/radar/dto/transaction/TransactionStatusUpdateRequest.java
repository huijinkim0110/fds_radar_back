package fds.radar.dto.transaction;


import fds.radar.common.TransactionStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class TransactionStatusUpdateRequest {
    
    @NotNull(message = "변경할 상태는 필수입니다.")
    private TransactionStatus status; // APPROVED/HOLD/CANCELLED

    @Size(max = 200, message = "사유는 최대 200자입니다")
    private String reason;                  //  보류·취소 사유

    public TransactionStatusUpdateRequest() {}

    public TransactionStatus getStatus() { return status; }
    public String getReason() { return reason; }

}
