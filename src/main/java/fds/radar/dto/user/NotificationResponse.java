package fds.radar.dto.user;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationResponse {
    private Long id;
    private String title;
    private String content;
    private boolean isRead;
    private LocalDateTime createAt;
}
