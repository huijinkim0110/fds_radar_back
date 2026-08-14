package fds.radar.dto.financial;

import fds.radar.common.IncomeSource;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FinancialProfileResponse {
    private Long financialProfileId;
    private String occupation;
    private IncomeSource incomeSource;
    private Long monthlyIncome;
    private Long monthlyExpenses;
    private Integer creditLevel;
    private Long availableMonthlyAmount;
    private Long emergencyFundAmount;
}
