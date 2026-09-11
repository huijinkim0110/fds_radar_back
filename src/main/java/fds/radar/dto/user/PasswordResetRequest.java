package fds.radar.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PasswordResetRequest {
    
    // 비밀번호 찾기 (이메일 또는 휴대폰)
    @NotBlank(message = "비밀번호 찾기 방법을 선택해주세요.")
    private String verificationType;

    // 선택한 방법으로 이메일 또는 전화번호 입력
    @NotBlank(message = "이메일 또는 전화번호를 입력해주세요.")
    private String verificationValue;

    // 세 비밀번호
    @NotBlank(message = "새 비밀번호를 입력해주세요.")
    @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.")
    private String newPassword;

    public PasswordResetRequest() {
    }

    public String getVerificationType() {
        return verificationType;
    }

    public void setVerificationType(String verificationType) {
        this.verificationType = verificationType;
    }

    public String getVerificationValue() {
        return verificationValue;
    }

    public void setVerificationValue(String verificationValue) {
        this.verificationValue = verificationValue;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}