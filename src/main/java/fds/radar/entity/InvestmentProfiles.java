package fds.radar.entity;

import java.time.LocalDateTime;

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
    @Column(name = "risk_tendency", nullable = false)
    private RiskTendency riskTendency;

    public enum RiskTendency {
        STABLE,
        NEUTRAL,
        ACTIVE,
        AGGRESSIVE
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "investment_experience", nullable = false)
    private InvestmentExperience investmentExperience;

    public enum InvestmentExperience {
        NONE,
        BEGINNER,
        INTERMEDIATE,
        EXPERIENCED
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "loss_tolerance", nullable = false)
    private LossTolerance lossTolerance;

    public enum LossTolerance {
        NONE,
        LOW,
        MEDIUM,
        HIGH
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "preferred_period", nullable = false)
    private PreferredPeriod preferredPeriod;

    public enum PreferredPeriod {
        SHORT_TERM,
        MEDIUM_TERM,
        LONG_TERM
    }

    @Column(nullable = false)
    private boolean pricipalProtectionPreference;

    @Column(nullable = false)
    private Integer diagnosisScore;

    @Column(nullable = false)
    private LocalDateTime diagnosedAt;
}
