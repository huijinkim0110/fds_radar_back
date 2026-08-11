package fds.radar.repository.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import fds.radar.entity.user.Users;

public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByEmail(String Email); // 로그인 시 이메일 유저 조회
    boolean exexistsByEmail(String email); // 회원가입 시 중복 이메일 체크
}
