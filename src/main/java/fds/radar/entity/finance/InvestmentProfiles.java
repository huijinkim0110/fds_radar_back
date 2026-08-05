package fds.radar.entity.finance;

import java.time.LocalDateTime;

import fds.radar.entity.user.Users;
import fds.radar.common.InvestmentExperience;
import fds.radar.common.LossTolerance;
import fds.radar.common.PreferredPeriod;
import fds.radar.common.RiskTendency;
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
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvestmentProfiles {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long investmentProfileId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RiskTendency riskTendency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvestmentExperience investmentExperience;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LossTolerance lossTolerance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PreferredPeriod preferredPeriod;

    @Column(nullable = false)
    private boolean pricipalProtectionPreference;

    @Column(nullable = false)
    private Integer diagnosisScore;

    @Column(nullable = false)
    private LocalDateTime diagnosedAt;
}
