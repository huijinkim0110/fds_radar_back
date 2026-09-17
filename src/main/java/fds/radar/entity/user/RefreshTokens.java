package fds.radar.entity.user;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity 
@Getter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder 
public class RefreshTokens {
    
    @Id 
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long tokenId;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name = "user_id", nullable = false)
    private Users user;

    @Column (nullable=false, unique=true, length=500)
    private String token;

    @Column (nullable=false)
    private LocalDateTime expiresAt;

    // 로그아웃/강제 종료 시 즉시 무효화
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
}
