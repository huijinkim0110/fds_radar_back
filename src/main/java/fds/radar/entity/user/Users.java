package fds.radar.entity.user;

import java.time.LocalDateTime;
import java.util.Date;

import fds.radar.common.UserRole;
import fds.radar.common.UserStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Users {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long userId;
    @Column(nullable=false, unique=true)
    private String loginId;
    @Column(nullable=false)
    private String password;
    @Column(nullable=false)
    private String email;
    @Column(nullable=false)
    private String name;
    
    private Date birthDate;
    private String phone;

    @Enumerated(EnumType.STRING)
    private UserRole role;
    @Enumerated(EnumType.STRING)
    private UserStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime passwordUpdatedAt;
    private LocalDateTime lastLoginAt;
    private LocalDateTime withdrawnAt;
}
