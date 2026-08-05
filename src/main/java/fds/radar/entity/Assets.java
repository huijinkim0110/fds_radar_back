package fds.radar.entity;

import java.time.LocalDateTime;

import fds.radar.common.AssetType;
import fds.radar.entity.user.Users;
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
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Assets {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long assetId;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=false)
    private Users user;

    @Enumerated(EnumType.STRING)
    private AssetType assetType;

    private String assetName;
    private String institutionName;
    private Long currentValue;

    private LocalDateTime evaluatedAt;
    private LocalDateTime createdAt;
}
