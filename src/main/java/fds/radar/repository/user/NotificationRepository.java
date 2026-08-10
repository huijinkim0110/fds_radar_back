package fds.radar.repository.user;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import fds.radar.entity.user.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserdOrderByCreatedAtDesc(Long userId);
    List<Notification> findByUserIdAndIsReadFalse(Long userId);
    
}
