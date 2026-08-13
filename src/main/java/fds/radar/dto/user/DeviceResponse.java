package fds.radar.dto.user;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DeviceResponse {
    private Long deviceId;
    private String deviceName;
    private boolean trusted;
    private boolean blocked;
}
