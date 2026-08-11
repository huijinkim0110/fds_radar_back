package fds.radar.dto;

import fds.radar.common.CardStatus;
import jakarta.validation.constraints.NotNull;

public class CardStatusUpdateRequest {
    
    @NotNull(message = "변경할 상태는 필수입니다.")
    private CardStatus status;

    public CardStatusUpdateRequest() {}

    public CardStatusUpdateRequest(CardStatus status) {
        this.status = status;
    }

    public CardStatus geStatus () {return status;}

}
