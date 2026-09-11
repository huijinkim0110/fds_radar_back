package fds.radar.dto.user;

import java.time.LocalDateTime;

import fds.radar.entity.user.Users;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminUserListResponse {

    private Long userId;
    private String name;
    private String email;
    private String role;
    private String status;
    private LocalDateTime createdAt;

    public static AdminUserListResponse from(Users user) {
        return new AdminUserListResponse(
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name(),
                user.getStatus().name(),
                user.getCreatedAt()
        );
    }
}