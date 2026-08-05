package fds.radar.entity;

import java.time.LocalDateTime;

import fds.radar.common.DeviceType;
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
@AllArgsConstructor
@Builder
public class UserDevices {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long deviceId;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=false)
    private Users user;

    private String deviceName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeviceType deviceType;

    private String deviceIdentifier;

    @Builder.Default
    private Boolean trusted = false;

    @Builder.Default
    private Boolean blocked = false;

    private LocalDateTime lastloginAt;
    private LocalDateTime registeredAt;

}
