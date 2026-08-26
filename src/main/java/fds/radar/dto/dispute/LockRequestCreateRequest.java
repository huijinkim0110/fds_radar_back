package fds.radar.dto.dispute;

import fds.radar.common.RequestTargetType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Setter;

@Setter
@Builder
@AllArgsConstructor
public class LockRequestCreateRequest {
    
    @NotNull(message = "fraudCardId는 필수입니다.")
    private Long farudCaseId;

    @NotNull(message = "잠금 대상 종류는 필수입니다.")
    private RequestTargetType targetType;

    @Size(max = 200, message = "사유는 최대 200자입니다.")
    private String requestReason;

    @NotNull(message = "targetId는 필수입니다")
    private Long targetId;

    public Long getTargetId() { return targetId; }
    
    public LockRequestCreateRequest() {}

    public Long getFraudCaseId() {return farudCaseId;}
    public RequestTargetType getTargetType() {return targetType;}
    public String getRequestReason() {return requestReason;}
}
