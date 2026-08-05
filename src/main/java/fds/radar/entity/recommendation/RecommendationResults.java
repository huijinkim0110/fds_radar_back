package fds.radar.entity.recommendation;

import java.time.LocalDateTime;

import fds.radar.entity.finance.FinancialGoals;
import fds.radar.entity.finance.InvestmentProfiles;
import fds.radar.entity.user.Users;
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
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecommendationResults {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long recommendationResultId;
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=false)
    private Users user;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="goal_id", nullable=false)
    private FinancialGoals goal;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="investment_profile_id", nullable=false)
    private InvestmentProfiles investmentProfile;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="model_id", nullable=false)
    private AiModels model;
    
    private LocalDateTime requestedAt;
    private LocalDateTime completedAt;

}
