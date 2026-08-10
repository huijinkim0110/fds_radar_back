package fds.radar.repository.user;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.User;

import fds.radar.entity.user.UserDevices;

public interface UserDeviceRepository extends JpaRepository<User, Long> {
    List<UserDevices> findByUserId(Long userId);
    Optional<UserDevices> findByUserIdAndDeviceId(String userId, String deviceId);
    
}
