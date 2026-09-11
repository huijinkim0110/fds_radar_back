package fds.radar.service.user;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fds.radar.common.UserStatus;
import fds.radar.dto.user.AdminUserListResponse;
import fds.radar.dto.user.UserStatusUpdateRequest;
import fds.radar.entity.user.Users;
import fds.radar.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;

// [D파트 담당자 추가] 관리자 전용 회원 관리 서비스 — 기존 UserService와 분리하여 충돌 최소화
@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserRepository userRepository;

    // 전체 회원 목록 조회
    @Transactional(readOnly = true)
    public List<AdminUserListResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(AdminUserListResponse::from)
                .toList();
    }

    // 회원 상태 변경 (정지/해제)
    @Transactional
    public AdminUserListResponse updateUserStatus(Long userId, UserStatusUpdateRequest request) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        UserStatus newStatus;
        try {
            newStatus = UserStatus.valueOf(request.getStatus());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("올바르지 않은 상태값입니다.");
        }

        user.setStatus(newStatus);
        userRepository.save(user);

        return AdminUserListResponse.from(user);
    }
}