package fds.radar.dto.user;

import java.time.LocalDateTime;

import fds.radar.entity.user.Notification;

public class NotificationResponse {

    private Long id;
    private String title;
    private String content;
    private boolean isRead;
    private LocalDateTime createAt;

    public NotificationResponse() {}

    public NotificationResponse(
            Long id,
            String title,
            String content,
            boolean isRead,
            LocalDateTime createAt) {

        this.id = id;
        this.title = title;
        this.content = content;
        this.isRead = isRead;
        this.createAt = createAt;
    }

    public static NotificationResponse from(Notification notification) {
        return new NotificationResponse(
                notification.getNotificationId(),
                notification.getNotificationTitle(),
                notification.getNotificationContent(),
                notification.isReadStatus(),
                notification.getCreatedAt()
        );
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public boolean isRead() {
        return isRead;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }
}