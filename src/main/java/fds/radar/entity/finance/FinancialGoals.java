package fds.radar.entity.finance;

import java.time.LocalDateTime;

import fds.radar.common.GoalStatus;
import fds.radar.common.GoalType;
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
public class FinancialGoals {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long goalId;
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=false)
    private Users userId;

    @Enumerated(EnumType.STRING)
    private GoalType goalType;
    private String goalName;
    private Long targetAmount;
    private Long currentAmount;
    private LocalDateTime targetDate;
    private Long monthlyTargetAmount;
    // 목표를 설정하면 자동적으로 진행중 상태
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private GoalStatus goalStatus = GoalStatus.IN_PROGRESS;

    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
}
