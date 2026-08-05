package fds.radar.entity;

import java.time.LocalDateTime;

import fds.radar.common.DeviceType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_devices")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserDevices {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "device_Id")
    private Long deviceId;

    @Column(name = "User_id", nullable = false)
    private Long userId;

    @Column(name = "device_name", nullable = false)
    private String deviceName;

    @Enumerated(EnumType.STRING)
    @Column(name = "device_type", nullable = false)
    private DeviceType deviceType;

    @Column(name = "device_identifier")
    private String deviceIdentifier;

    @Column(name = "trusted")
    @Builder.Default
    private Boolean trusted = false;

    @Column(name = "blocked")
    @Builder.Default
    private Boolean blocked = false;

    @Column(name = "last_login_at")
    private LocalDateTime lastloginAt;

    @Column(name = "registered_at")
    private LocalDateTime registeredAt;

    @PrePersist
    public void PrePersist() {
        this.registeredAt = LocalDateTime.now();
    }

}
