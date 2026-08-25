package fds.radar.dto.user;

import fds.radar.entity.user.Users;

public class SignUpResponse {

    private Long userId;
    private String email;
    private String name;

    public SignUpResponse() {}

    public SignUpResponse(Long userId, String email, String name) {
        this.userId = userId;
        this.email = email;
        this.name = name;
    }

    public static SignUpResponse from(Users user) {
        return new SignUpResponse(
            user.getUserId(),
            user.getEmail(),
            user.getName()
        );
    }

    public Long getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }
}