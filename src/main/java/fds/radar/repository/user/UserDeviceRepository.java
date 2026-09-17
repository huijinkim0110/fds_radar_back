package fds.radar.repository.user;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import fds.radar.entity.user.UserDevices;

public interface UserDeviceRepository extends JpaRepository<UserDevices, Long> {

    List<UserDevices> findByUser_UserId(Long userId);

    Optional<UserDevices> findByUser_UserIdAndDeviceIdentifier(
            Long userId,
            String deviceIdentifier
    );

    // 신뢰/차단 처리 시 본인 소유 기기 검증용
    Optional<UserDevices> findByDeviceIdAndUser_UserId(Long deviceId, Long userId);
}