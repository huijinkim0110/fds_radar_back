package fds.radar.dto.dispute;

import fds.radar.common.LockRequestStatus;
import jakarta.validation.constraints.NotNull;

public class LockRequestProcessRequest {
    
    @NotNull(message = "처리 상태는 필수입니다.")
    private LockRequestStatus requestStatus;

    public LockRequestProcessRequest() {}

    public LockRequestStatus getRequestStatus() {return requestStatus;}
}
