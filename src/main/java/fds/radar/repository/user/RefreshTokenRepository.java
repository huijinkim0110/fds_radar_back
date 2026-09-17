package fds.radar.repository.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import fds.radar.entity.user.RefreshTokens;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokens, Long> {
    
    Optional<RefreshTokens> findByToken(String token);

    void deleteByToken(String token);

    void deleteByUser_UserId(Long userId); // 로그아웃/비번 변경 시 해당 유저 토큰 전체 삭제
}
