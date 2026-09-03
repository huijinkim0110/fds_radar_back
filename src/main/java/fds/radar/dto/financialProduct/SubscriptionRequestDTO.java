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
    private Long accountId; // 대금이 빠져나갈 계좌
    private Long goalId; // 이 가입이 기여할 재무목표(선택사항, 안넘기면 미연동)
    private Long subscriptionAmount; // 가입금액
    private Integer subscriptionPeriod; // 가입기간(개월)
}
