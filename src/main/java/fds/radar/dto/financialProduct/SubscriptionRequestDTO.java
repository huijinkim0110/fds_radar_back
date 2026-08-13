package fds.radar.dto.financialProduct;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionRequestDTO {
    private Long userId;
    private Long productId;
    private Long subscriptionAmount; // 가입금액
    private Integer subscriptionPeriod; // 가입기간(개월)
}
