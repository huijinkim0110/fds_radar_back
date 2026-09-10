package fds.radar.dto.financialProduct;

import fds.radar.common.PaymentMethod;
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
    private PaymentMethod paymentMethod; // 일시납/월납/혼합 - 사용자가 선택
    private Long initialAmount; // 일시납 초기 금액(LUMP_SUM, MIXED일 때 필수, INSTALLMENT인 경우 null)
    private Long monthlyPayment; // 월 납입액(INSTALLMENT, MIXED일 때 필수, LUMP_SUM인 경우 null)
    private Integer subscriptionPeriod; // 가입기간(개월)
}
