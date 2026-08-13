package fds.radar.service.finance;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fds.radar.common.GoalStatus;
import fds.radar.dto.finance.FinancialGoalRequestDTO;
import fds.radar.dto.finance.FinancialGoalResponseDTO;
import fds.radar.entity.finance.FinancialGoals;
import fds.radar.entity.user.Users;
import fds.radar.repository.finance.FinancialGoalsRepository;
import fds.radar.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FinancialGoalsService {
    
    private final FinancialGoalsRepository financialGoalsRepository;
    private final UserRepository userRepository;

    // 금융목표 등록
    // - 등록 시점엔 진행금액 0원, 상태는 IN_PROGRESS(기본값)
    @Transactional
    public FinancialGoalResponseDTO createGoal(FinancialGoalRequestDTO dto) {
        Users user = userRepository.findById(dto.getUserId())
                                   .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        FinancialGoals goal = FinancialGoals.builder()
                                            .user(user)
                                            .goalType(dto.getGoalType())
                                            .goalName(dto.getGoalName())
                                            .targetAmount(dto.getTargetAmount())
                                            .currentAmount(0L)
                                            .targetDate(dto.getTargetDate())
                                            .monthlyTargetAmount(dto.getMonthlyTargetAmount())
                                            .createdAt(LocalDateTime.now())
                                            .build();

        FinancialGoals saved = financialGoalsRepository.save(goal);
        return toResponseDTO(saved);
    }

    // 목표별 진행금액 수정
    // - 목표금액 이상으로 채워지면 자동으로 ACHIEVED 처리
    @Transactional
    public FinancialGoalResponseDTO updateCurrentAmount(Long goalId, Long newCurrentAmount) {
        FinancialGoals goal = financialGoalsRepository.findById(goalId)
                                                      .orElseThrow(() -> new IllegalArgumentException("목표를 찾을 수 없습니다."));

        goal.setCurrentAmount(newCurrentAmount);

        if (newCurrentAmount >= goal.getTargetAmount() && goal.getGoalStatus() == GoalStatus.IN_PROGRESS) {
            goal.setGoalStatus(GoalStatus.ACHIEVED);
            goal.setCompletedAt(LocalDateTime.now());
        }

        return toResponseDTO(goal);
    }

    // 목표 취소
    @Transactional
    public void cancelGoal(Long goalId) {
        FinancialGoals goal = financialGoalsRepository.findById(goalId)
                                                      .orElseThrow(() -> new IllegalArgumentException("목표를 찾을 수 없습니다."));

        goal.setGoalStatus(GoalStatus.CANCELLED);
    }

    // 목표 목록 조회 - 기본적으로 취소된 목표는 숨김
    // includeCancelled=true면 전체 조회
    @Transactional(readOnly=true)
    public List<FinancialGoalResponseDTO> getGoals(Long userId, boolean includeCancelled) {
        List<FinancialGoals> goals = includeCancelled
                ? financialGoalsRepository.findByUser_UserId(userId)
                : financialGoalsRepository.findByUser_UserIdAndGoalStatusNot(userId, GoalStatus.CANCELLED);

        return goals.stream()
                    .map(this::toResponseDTO)
                    .toList();
    }

    private FinancialGoalResponseDTO toResponseDTO(FinancialGoals goal) {
        double rate = goal.getTargetAmount() == 0
                          ? 0.0
                          : (double) goal.getCurrentAmount() / goal.getTargetAmount() * 100;

        return FinancialGoalResponseDTO.builder()
                                       .goalId(goal.getGoalId())
                                       .goalType(goal.getGoalType())
                                       .goalName(goal.getGoalName())
                                       .targetAmount(goal.getTargetAmount())
                                       .currentAmount(goal.getCurrentAmount())
                                       .targetDate(goal.getTargetDate())
                                       .monthlyTargetAmount(goal.getMonthlyTargetAmount())
                                       .goalStatus(goal.getGoalStatus())
                                       .achievementRate(Math.round(rate * 10) / 10.0)
                                       .build();
    }
}
