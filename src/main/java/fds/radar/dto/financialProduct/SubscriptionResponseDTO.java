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
    private Long subscriptionAmount;
    private Long monthlyPayment;
    private Integer subscriptionPeriod;
    private Long expectedMaturityAmount;
    private SubscriptionStatus subscriptionStatus;
    private LocalDateTime subscribedAt;
}
