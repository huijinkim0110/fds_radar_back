package fds.radar.repository.finance;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import fds.radar.common.GoalStatus;
import fds.radar.entity.finance.FinancialGoals;

public interface FinancialGoalsRepository extends JpaRepository<FinancialGoals, Long> {
    // 특정 상태(예: CANCELLED)를 제외한 목표 목록 조회
    List<FinancialGoals> findByUser_UserIdAndGoalStatusNot(Long userId, GoalStatus excludedStatus);

    // 사용자의 전체 목표 조회
    List<FinancialGoals> findByUser_UserId(Long userId);

    // 여러 목표 중 진행 중(IN_PROGRESS 상태의 가장 최근 목표 1건 - 추천/적합성 검사 자동 반영용
    Optional<FinancialGoals> findFirstByUser_UserIdAndGoalStatusOrderByCreatedAtDesc(Long userId, GoalStatus goalStatus);

    // 본인 소유 목표 검증용 - 모의가입 연동 시 타인 목표 연결 차단
    Optional<FinancialGoals> findByGoalIdAndUser_UserId(Long goalId, Long userId);
}
