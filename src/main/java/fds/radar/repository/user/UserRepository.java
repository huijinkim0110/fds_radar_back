package fds.radar.repository.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String Email); // 로그인시 이메일로 유저 조회
    boolean existsByEmail(String email); // 회원가입 시 중복 이메일 체크
    
}
