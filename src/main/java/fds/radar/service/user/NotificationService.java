package fds.radar.service.user;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fds.radar.dto.user.NotificationRequest;
import fds.radar.dto.user.NotificationResponse;
import fds.radar.entity.user.Notification;
import fds.radar.entity.user.Users;
import fds.radar.repository.user.NotificationRepository;
import fds.radar.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    // 알림 생성
    @Transactional
    public NotificationResponse createNotification(
            Long userId,
            NotificationRequest request) {

        Users user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "사용자를 찾을 수 없습니다."
                        )
                );

        Notification notification = Notification.builder()
                .user(user)
                .notificationType(request.getNotificationType())
                .notificationTitle(request.getNotificationTitle())
                .notificationContent(request.getNotificationContent())
                .relatedType(request.getRelatedType())
                .relatedId(request.getRelatedId())
                .readStatus(false)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);

        return NotificationResponse.from(notification);
    }

    // 사용자의 알림 전체 조회
    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotifications(Long userId) {

        return notificationRepository
                .findByUser_UserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(NotificationResponse::from)
                .collect(Collectors.toList());
    }

    // 특정 알림 읽음 처리
    @Transactional
    public void readNotification(Long userId, Long notificationId) {

        Notification notification = notificationRepository
                .findByNotificationIdAndUser_UserId(
                        notificationId,
                        userId
                )
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "알림을 찾을 수 없습니다."
                        )
                );

        notification.setReadStatus(true);
    }
}