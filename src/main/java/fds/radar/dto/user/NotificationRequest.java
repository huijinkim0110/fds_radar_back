package fds.radar.dto.user;

import fds.radar.common.NotificationType;
import fds.radar.common.RelatedType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class NotificationRequest {

    @NotNull(message = "알림 유형은 필수입니다.")
    private NotificationType notificationType;

    @NotBlank(message = "알림 제목은 필수입니다.")
    private String notificationTitle;

    @NotBlank(message = "알림 내용은 필수입니다.")
    private String notificationContent;

    private RelatedType relatedType;

    private Long relatedId;

    public NotificationRequest() {}

    public NotificationRequest(
            NotificationType notificationType,
            String notificationTitle,
            String notificationContent,
            RelatedType relatedType,
            Long relatedId) {

        this.notificationType = notificationType;
        this.notificationTitle = notificationTitle;
        this.notificationContent = notificationContent;
        this.relatedType = relatedType;
        this.relatedId = relatedId;
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public String getNotificationTitle() {
        return notificationTitle;
    }

    public String getNotificationContent() {
        return notificationContent;
    }

    public RelatedType getRelatedType() {
        return relatedType;
    }

    public Long getRelatedId() {
        return relatedId;
    }
}