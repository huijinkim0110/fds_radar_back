package fds.radar.dto.user;

import jakarta.validation.constraints.NotBlank;

public class DeviceRegisterRequest {

    @NotBlank(message = "디바이스 ID는 필수입니다.")
    private String deviceId;

    @NotBlank(message = "디바이스 이름은 필수입니다.")
    private String deviceName;

    public DeviceRegisterRequest() {}

    public DeviceRegisterRequest(String deviceId, String deviceName) {
        this.deviceId = deviceId;
        this.deviceName = deviceName;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }
}