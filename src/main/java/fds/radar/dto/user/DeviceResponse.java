package fds.radar.dto.user;

import fds.radar.entity.user.UserDevices;

public class DeviceResponse {

    private Long deviceId;
    private String deviceName;
    private boolean trusted;
    private boolean blocked;

    public DeviceResponse() {}

    public DeviceResponse(Long deviceId, String deviceName,
                           boolean trusted, boolean blocked) {
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.trusted = trusted;
        this.blocked = blocked;
    }

    public static DeviceResponse from(UserDevices device) {
        return new DeviceResponse(
                device.getDeviceId(),
                device.getDeviceName(),
                device.getTrusted(),
                device.getBlocked()
        );
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public boolean isTrusted() {
        return trusted;
    }

    public boolean isBlocked() {
        return blocked;
    }
}