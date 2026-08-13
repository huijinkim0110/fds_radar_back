package fds.radar.dto.user;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SignUpResponse {
    private Long userId;
    private String name;
}
