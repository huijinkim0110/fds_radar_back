package fds.radar.controller.finance;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fds.radar.dto.finance.FinancialGoalRequestDTO;
import fds.radar.dto.finance.FinancialGoalResponseDTO;
import fds.radar.service.finance.FinancialGoalsService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/financial-goals")
@RequiredArgsConstructor
public class FInancialGoalsController {
    
    private final FinancialGoalsService financialGoalsService;

    // 금융목표 등록
    // POST /financial-goals
    @PostMapping
    public ResponseEntity<FinancialGoalResponseDTO> createGoal(@RequestBody FinancialGoalRequestDTO dto) {
        FinancialGoalResponseDTO result = financialGoalsService.createGoal(dto);
        return ResponseEntity.ok(result);
    }

    // 목표 진행금액 수정
    // PATCH /financial-goals/{goalId}/current-amount?amount=5000000
    @PatchMapping("/{goalId}/current-amount")
    public ResponseEntity<FinancialGoalResponseDTO> updateCurrentAmount(
            @PathVariable Long goalId,
            @RequestParam Long amount) {
               
        FinancialGoalResponseDTO result = financialGoalsService.updateCurrentAmount(goalId, amount);
        return ResponseEntity.ok(result);
    }

    // 목표 취소
    // PATCH /financial-goals/{goalId}/cancel
    @PatchMapping("/{goalId}/cancel")
    public ResponseEntity<Void> cancelGoal(@PathVariable Long goalId) {
        financialGoalsService.cancelGoal(goalId);
        return ResponseEntity.noContent().build();
    }

    // 목표 목록 조회(기본 : 취소된 목표 숨김)
    // GET /financial-goals?userId=1&includeCancelled=false
    @GetMapping
    public ResponseEntity<List<FinancialGoalResponseDTO>> getGoals(
            @RequestParam Long userId,
            @RequestParam(defaultValue="false") boolean includeCancelled) {

        List<FinancialGoalResponseDTO> result = financialGoalsService.getGoals(userId, includeCancelled);
        return ResponseEntity.ok(result);
    }

}
