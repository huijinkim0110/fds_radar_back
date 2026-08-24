package fds.radar.service.user;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fds.radar.dto.user.DeviceRegisterRequest;
import fds.radar.dto.user.DeviceResponse;
import fds.radar.entity.user.UserDevices;
import fds.radar.entity.user.Users;
import fds.radar.repository.user.UserDeviceRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDeviceService {

    private final UserDeviceRepository userDeviceRepository;

    // 디바이스 등록
    @Transactional
    public DeviceResponse registerOrUpdateDevice(
            Long userId,
            DeviceRegisterRequest request) {

        UserDevices devices = userDeviceRepository
                .findByUser_UserIdAndDeviceIdentifier(
                        userId,
                        request.getDeviceId()
                )
                .orElseGet(() -> UserDevices.builder()
                        .user(
                                Users.builder()
                                        .userId(userId)
                                        .build()
                        )
                        .deviceName(request.getDeviceName())
                        .deviceIdentifier(request.getDeviceId())
                        .trusted(false)
                        .blocked(false)
                        .build()
                );

        userDeviceRepository.save(devices);

        return DeviceResponse.from(devices);
    }

    // 등록된 디바이스 전체 조회
    @Transactional(readOnly = true)
    public List<DeviceResponse> getDevices(Long userId) {

        return userDeviceRepository.findByUser_UserId(userId)
                .stream()
                .map(DeviceResponse::from)
                .collect(Collectors.toList());
    }

    // 신뢰 기기 등록
    @Transactional
    public void trustDevice(Long deviceId) {

        UserDevices devices = userDeviceRepository.findById(deviceId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "신뢰기기로 등록할 기기를 찾을 수 없습니다."
                        )
                );

        devices.trust();
    }

    // 기기 차단
    @Transactional
    public void blockDevice(Long deviceId) {

        UserDevices devices = userDeviceRepository.findById(deviceId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "차단할 기기를 찾을 수 없습니다."
                        )
                );

        devices.block();
    }
}