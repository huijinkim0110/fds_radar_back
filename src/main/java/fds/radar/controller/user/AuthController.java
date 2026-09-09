package fds.radar.controller.user;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fds.radar.dto.user.EmailCheckRequest;
import fds.radar.dto.user.LoginRequest;
import fds.radar.dto.user.LoginResponse;
import fds.radar.dto.user.SignUpRequest;
import fds.radar.dto.user.SignUpResponse;
import fds.radar.dto.user.PasswordResetRequest;
import fds.radar.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<SignUpResponse> signUp(
            @Valid @RequestBody SignUpRequest request) {

        SignUpResponse response = userService.signup(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {

        LoginResponse response = userService.login(request);

        return ResponseEntity.ok(response);
    }

    //  비밀번로 재설정
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
        @Valid @RequestBody PasswordResetRequest request) {

            userService.resetPassword(request);

            return ResponseEntity.ok("비밀번호가 변경되었습니다.");
        }

    // 가입된 이메일 확인
    @PostMapping("/check-email")
    public ResponseEntity<Map<String, Boolean>> checkEmail(
            @Valid @RequestBody EmailCheckRequest request) {

        boolean exists = userService.checkEmail(request.getEmail());

        return ResponseEntity.ok(
                Map.of("exists", exists)
        );
    }
}