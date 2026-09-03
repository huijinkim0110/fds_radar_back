package fds.radar.dto.financialProduct;

import java.time.LocalDateTime;

import fds.radar.common.SubscriptionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionResponseDTO {
    private Long simulatedSubscriptionId;
    private String productName;
    private Long accountId;
    private String accountNumber; // 마스킹된 계좌번호
    private Long goalId; // 연동된 재무목표(없으면 null)
    private String goalName;
    private Long subscriptionAmount;
    private Long monthlyPayment;
    private Integer subscriptionPeriod;
    private Long expectedMaturityAmount;
    private Long paidAmount; // 실제 누적 납입액
    private Integer paidInstallments; // 적금만 사용
    private Double achievementRate; // 달성률(%) = paidAmount / 목표납입액 * 100
    private LocalDateTime nextPaymentDate; // 적금 다음 납입 예정일
    private SubscriptionStatus subscriptionStatus;
    private LocalDateTime subscribedAt;
    private LocalDateTime completedAt;
}
