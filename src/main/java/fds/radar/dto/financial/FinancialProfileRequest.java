package fds.radar.dto.financial;

import lombok.Getter;

@Getter
public class FinancialProfileRequest {
    private String occupation;
    private String employmentType;
    private Long monthlyIncome;
    private Long monthlyExpense;
    private String creditRating;
}
