package fds.radar.repository.finance;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import fds.radar.common.GoalStatus;
import fds.radar.entity.finance.FinancialGoals;

public interface FinancialGoalsRepository extends JpaRepository<FinancialGoals, Long> {
    // 특정 상태(예: CANCELLED)를 제외한 목표 목록 조회
    List<FinancialGoals> findByUser_UserIdAndGoalStatusNot(Long userId, GoalStatus excludedStatus);

    // 사용자의 전체 목표 조회
    List<FinancialGoals> findByUser_UserId(Long userId);
}
