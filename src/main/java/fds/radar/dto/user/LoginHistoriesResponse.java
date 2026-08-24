package fds.radar.dto.user;

import java.time.LocalDateTime;

import fds.radar.common.LoginResult;
import fds.radar.entity.user.LoginHistories;

public class LoginHistoriesResponse {

    private Long id;
    private String ipAddress;
    private String deviceInfo;
    private boolean success;
    private LocalDateTime createAt;

    public LoginHistoriesResponse() {}

    public LoginHistoriesResponse(
            Long id,
            String ipAddress,
            String deviceInfo,
            boolean success,
            LocalDateTime createAt) {

        this.id = id;
        this.ipAddress = ipAddress;
        this.deviceInfo = deviceInfo;
        this.success = success;
        this.createAt = createAt;
    }

    public static LoginHistoriesResponse from(LoginHistories history) {

        return new LoginHistoriesResponse(
                history.getLoginHistoryId(),
                history.getIpAddress(),
                history.getUserDevice().getDeviceName(),
                history.getLoginResult() == LoginResult.SUCCESS,
                history.getAttemptedAt()
        );
    }

    public Long getId() {
        return id;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public boolean isSuccess() {
        return success;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }
}