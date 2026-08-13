package fds.radar.dto.finance;

import java.time.LocalDateTime;

import fds.radar.common.GoalStatus;
import fds.radar.common.GoalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinancialGoalResponseDTO {
    private Long goalId;
    private GoalType goalType;
    private String goalName;
    private Long targetAmount;
    private Long currentAmount;
    private LocalDateTime targetDate;
    private Long monthlyTargetAmount;
    private GoalStatus goalStatus;
    private Double achievementRate; // 달성률(%) - 조회 시점에 계산해서 내려줌
}
