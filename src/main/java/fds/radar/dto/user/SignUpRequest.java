package fds.radar.dto.user;

import lombok.Getter;

@Getter
public class SignUpRequest {
    private String email;
    private String Password;
    private String name;
    private String birthDete;
    private String phoneNumber;
}
