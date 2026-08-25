package fds.radar.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class SignUpRequest {

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.")
    private String password;

    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    @NotBlank(message = "생년월일은 필수입니다.")
    private String birthDete;

    @NotBlank(message = "전화번호는 필수입니다.")
    @Pattern(
        regexp = "^\\d{3}-\\d{3,4}-\\d{4}$",
        message = "올바른 전화번호 형식이 아닙니다."
    )
    private String phoneNumber;

    public SignUpRequest() {}

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getBirthDete() {
        return birthDete;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}