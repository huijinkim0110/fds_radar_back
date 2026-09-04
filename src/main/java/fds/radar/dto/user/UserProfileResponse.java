package fds.radar.dto.user;

import java.util.Date;

import fds.radar.entity.user.Users;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserProfileResponse {

    private Long userId;
    private String name;
    private String email;
    private String phone;
    private Date birthDate;
    private String role;

    public static UserProfileResponse from(Users user) {
        return new UserProfileResponse(
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getBirthDate(),
                user.getRole().name()
        );
    }
}