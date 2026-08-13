package fds.radar.dto.user;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginHistoriesResponse {
    private Long id;
    private String ipAddress;
    private String deviceInfo;
    private boolean success;
    private LocalDateTime createAt;
}
