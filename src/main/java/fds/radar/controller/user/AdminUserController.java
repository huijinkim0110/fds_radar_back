package fds.radar.controller.user;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fds.radar.dto.user.AdminUserListResponse;
import fds.radar.dto.user.UserStatusUpdateRequest;
import fds.radar.service.user.AdminUserService;
import lombok.RequiredArgsConstructor;

// [D파트 담당자 추가] 관리자 전용 회원 관리 컨트롤러 — 기존 UserController와 분리하여 충돌 최소화
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    // 전체 회원 목록 조회
    @GetMapping
    public ResponseEntity<List<AdminUserListResponse>> getAllUsers() {
        return ResponseEntity.ok(adminUserService.getAllUsers());
    }

    // 회원 상태 변경 (정지/해제)
    @PatchMapping("/{userId}/status")
    public ResponseEntity<AdminUserListResponse> updateUserStatus(
            @PathVariable Long userId,
            @RequestBody UserStatusUpdateRequest request
    ) {
        return ResponseEntity.ok(adminUserService.updateUserStatus(userId, request));
    }
}