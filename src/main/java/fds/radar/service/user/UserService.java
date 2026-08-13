package fds.radar.service.user;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fds.radar.dto.user.LoginRequest;
import fds.radar.dto.user.LoginResponse;
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

    @Transactional
    public SignUpResponse signUp(SiginUpRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalAccessException("이미 가입된 이메일입니다.");
        }

        Users users = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .birthDate(request.getBirthDate())
                .phoneNumber(request.getphoneNumber())
                .role("USER")
                .status("ACTIVE")
                .build();

        userRepository.save(users);

        return SignUpResponse.builder()
                .userId(users.getId())
                .email(users.getEmail())
                .name(users.getName())
                .build();
    }

    public LoginResponse login(LoginRequest request) {
        Users user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 계정입나다."));

        if (!passwordEncoder.matches((request.getPassword()), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        
        if ("SUSPENDED".equals(user.getStatus())) {
            throw new IllegalArgumentException("정지된 계정입니다.");
        }

        if ("WITHDRAWN".equals(user.getStatus())) {
            throw new IllegalArgumentException("탈퇴한 계정입니다.");
        }

        String token = jwtTokenProvider.createToken(user.getId, user.getRole());

        return LoginResponse.builder()
                .accessToken(token)
                .role(user.getRole())
                .build()
    }
}
