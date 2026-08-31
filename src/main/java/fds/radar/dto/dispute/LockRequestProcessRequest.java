package fds.radar.dto.dispute;

import fds.radar.common.LockRequestStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Setter;

@Setter
@Builder
@AllArgsConstructor
public class LockRequestProcessRequest {
    
    @NotNull(message = "처리 상태는 필수입니다.")
    private LockRequestStatus requestStatus;

    public LockRequestProcessRequest() {}

    public LockRequestStatus getRequestStatus() {return requestStatus;}
}
