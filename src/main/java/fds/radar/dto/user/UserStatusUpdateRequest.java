package fds.radar.dto.user;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserStatusUpdateRequest {
    private String status; // "ACTIVE", "SUSPENDED" 등 UserStatus enum 값
}