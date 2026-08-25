package fds.radar.service.user;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fds.radar.dto.user.LoginHistoriesRequest;
import fds.radar.dto.user.LoginHistoriesResponse;
import fds.radar.entity.user.LoginHistories;
import fds.radar.entity.user.UserDevices;
import fds.radar.entity.user.Users;
import fds.radar.repository.user.LoginHistoriesRepository;
import fds.radar.repository.user.UserDeviceRepository;
import fds.radar.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoginHistoryService {

    private final LoginHistoriesRepository loginHistoriesRepository;
    private final UserDeviceRepository userDeviceRepository;
    private final UserRepository userRepository;

    // 로그인 기록 저장
    @Transactional
    public void record(
            Long userId,
            String ipAddress,
            LoginHistoriesRequest request) {

        Users user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("사용자를 찾을 수 없습니다.")
                );

        UserDevices userDevice = userDeviceRepository.findById(
                request.getDeviceId()
        ).orElseThrow(() ->
                new IllegalArgumentException("디바이스를 찾을 수 없습니다.")
        );

        LoginHistories histories = LoginHistories.builder()
                .user(user)
                .userDevice(userDevice)
                .ipAddress(ipAddress)
                .loginResult(request.getLoginResult())
                .failureReason(request.getFailureReason())
                .attemptedAt(LocalDateTime.now())
                .build();

        loginHistoriesRepository.save(histories);
    }

    // 로그인 기록 조회
    @Transactional(readOnly = true)
    public List<LoginHistoriesResponse> getHistories(Long userId) {

        return loginHistoriesRepository
                .findByUser_UserIdOrderByAttemptedAtDesc(userId)
                .stream()
                .map(LoginHistoriesResponse::from)
                .collect(Collectors.toList());
    }
}