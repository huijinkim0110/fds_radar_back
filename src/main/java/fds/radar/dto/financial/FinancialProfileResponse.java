package fds.radar.dto.financial;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FinancialProfileResponse {
    private Long id;
    private String occupation;
    private String employmentType;
    private Long monthlyIncome;
    private Long monthlyExpense;
    private String creditRating;
    private Long savingCapacity;
}
