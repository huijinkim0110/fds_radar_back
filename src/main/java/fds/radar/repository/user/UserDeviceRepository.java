package fds.radar.repository.user;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import fds.radar.entity.user.UserDevices;

public interface UserDeviceRepository extends JpaRepository<UserDevices, Long> {
    List<UserDevices> findByUser_UserId(Long userId);
    Optional<UserDevices> findByUser_UserIdAndDeviceId(String userId, String deviceId);
    
}
