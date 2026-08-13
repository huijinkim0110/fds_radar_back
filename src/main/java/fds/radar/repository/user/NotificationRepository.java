package fds.radar.repository.user;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import fds.radar.entity.user.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUser_UserIdOrderByCreatedAtDesc(Long userId);
    List<Notification> findByUser_UserIdAndReadStatusFalse(Long userId);
    
}
