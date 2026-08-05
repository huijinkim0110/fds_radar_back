package fds.radar.entity;

import java.time.LocalDateTime;

import fds.radar.common.FailureReason;
import fds.radar.common.LoginResult;
import fds.radar.entity.user.UserDevices;
import fds.radar.entity.user.Users;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Builder
@AllArgsConstructor
public class LoginHistories {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long loginHistoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable=false)
    private UserDevices userDevice;

    @Column(nullable = false)
    private String ipAddress;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoginResult loginResult;

    @Enumerated(EnumType.STRING)
    private FailureReason failureReason;

    @Column(nullable = false)
    private LocalDateTime attemptedAt;

}
