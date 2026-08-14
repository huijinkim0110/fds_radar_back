package fds.radar.dto.user;

public class LoginResponse {

    private Long userId;
    private String email;
    private String name;
    private String role;
    private String accessToken;

    public LoginResponse() {}

    public LoginResponse(Long userId, String email, String name,
                         String role, String accessToken) {
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.role = role;
        this.accessToken = accessToken;
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

    public String getRole() {
        return role;
    }

    public String getAccessToken() {
        return accessToken;
    }
}