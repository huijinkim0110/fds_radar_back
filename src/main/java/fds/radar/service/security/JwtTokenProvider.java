package fds.radar.service.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long expirationMs;
    private final long refreshExpirationMs;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms") long expirationMs,
            @Value("${jwt.refresh-expiration-ms") long refreshExpirationMs
    ) {
        this.key = Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );

        this.expirationMs = expirationMs;
        this.refreshExpirationMs = refreshExpirationMs;
    }

    // 로그인 성공 시 JWT 토큰 생성
    public String createToken(Long userId, String role) {

        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key)
                .compact();
    }

    // 토큰 유효성 검증 (서명/만료 확인)
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // 토큰에서 userId 추출
    public Long getUserId(String token) {
        String subject = Jwts.parser()
                             .verifyWith(key)
                             .build()
                             .parseSignedClaims(token)
                             .getPayload()
                             .getSubject();

        return Long.valueOf(subject);
    }

    // 토큰에서 role 추출
    public String getRole(String token) {
        return Jwts.parser()
                   .verifyWith(key)
                   .build()
                   .parseSignedClaims(token)
                   .getPayload()
                   .get("role", String.class);
    }

    // Refresh Token 생성(subject만 담음, role은 accessToken 재발급 시 DB에서 다시 조회)
    public String createRefreshToken(Long userId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + refreshExpirationMs);

        return Jwts.builder()
                   .subject(String.valueOf(userId))
                   .issuedAt(now)
                   .expiration(expiry)
                   .signWith(key)
                   .compact();
    }

    public long getRefreshExpirationMs() {
        return refreshExpirationMs;
    }
}