package fds.radar.dto.financial;

import fds.radar.common.IncomeSource;
import lombok.Getter;

@Getter
public class FinancialProfileRequest {
    private Long userId;
    private String occupation;
    private IncomeSource incomeSource;
    private Long monthlyIncome;
    private Long monthlyExpenses;
    private Integer creditLevel;
    private Long emergencyFundAmount;
   

    // availableMonthlyAmount는 요청으로 안받고 서버에서 monthlyIncome - monthlyExpenses로 계산
}
