package fds.radar.dto.finance;

import java.time.LocalDateTime;

import fds.radar.common.GoalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinancialGoalRequestDTO {
    private Long userId;
    private GoalType goalType;
    private String goalName;
    private Long targetAmount;
    private LocalDateTime targetDate;
    private Long monthlyTargetAmount;
}
