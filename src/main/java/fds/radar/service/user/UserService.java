package fds.radar.service.user;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fds.radar.common.UserRole;
import fds.radar.common.UserStatus;
import fds.radar.dto.user.LoginRequest;
import fds.radar.dto.user.LoginResponse;
import fds.radar.dto.user.SignUpRequest;
import fds.radar.dto.user.SignUpResponse;
import fds.radar.entity.user.Users;
import fds.radar.repository.user.UserRepository;
import fds.radar.service.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    // 회원가입
    @Transactional
    public SignUpResponse signup(SignUpRequest request) {

        // 이메일 중복 확인
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        // 생년월일 String → Date 변환
        Date birthDate;

        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            format.setLenient(false);
            birthDate = format.parse(request.getBirthDete());

        } catch (ParseException e) {
            throw new IllegalArgumentException(
                    "생년월일은 yyyy-MM-dd 형식으로 입력해주세요."
            );
        }

        // 회원 객체 생성
        Users user = Users.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .birthDate(birthDate)
                .phone(request.getPhoneNumber())
                .role(UserRole.USER)
                .status(UserStatus.ACTIVE)
                .build();

        // DB 저장
        userRepository.save(user);

        // Response 반환
        return SignUpResponse.from(user);
    }

    // 로그인
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {

        // 이메일로 회원 조회
        Users user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 이메일입니다.")
                );

        // 비밀번호 확인
        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        )) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 계정 상태 확인
        if (user.getStatus() == UserStatus.SUSPENDED) {
            throw new IllegalArgumentException("정지된 계정입니다.");
        }

        if (user.getStatus() == UserStatus.WITHDRAWN) {
            throw new IllegalArgumentException("탈퇴한 계정입니다.");
        }

        // JWT 발급
        String token = jwtTokenProvider.createToken(
                user.getUserId(),
                user.getRole().name()
        );

        // 로그인 응답
        return new LoginResponse(
                user.getUserId(),
                user.getEmail(),
                user.getName(),
                user.getRole().name(),
                token
        );
    }
}