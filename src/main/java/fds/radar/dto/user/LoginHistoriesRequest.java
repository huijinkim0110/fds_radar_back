package fds.radar.dto.user;

import fds.radar.common.FailureReason;
import fds.radar.common.LoginResult;
import jakarta.validation.constraints.NotNull;

public class LoginHistoriesRequest {

    @NotNull(message = "디바이스는 필수입니다.")
    private Long deviceId;

    @NotNull(message = "로그인 결과는 필수입니다.")
    private LoginResult loginResult;

    private FailureReason failureReason;

    public LoginHistoriesRequest() {}

    public LoginHistoriesRequest(
            Long deviceId,
            LoginResult loginResult,
            FailureReason failureReason) {

        this.deviceId = deviceId;
        this.loginResult = loginResult;
        this.failureReason = failureReason;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public LoginResult getLoginResult() {
        return loginResult;
    }

    public FailureReason getFailureReason() {
        return failureReason;
    }
}