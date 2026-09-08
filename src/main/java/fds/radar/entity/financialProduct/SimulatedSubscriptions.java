package fds.radar.entity.financialProduct;

import java.time.LocalDateTime;

import fds.radar.common.SubscriptionStatus;
import fds.radar.entity.account.Accounts;
import fds.radar.entity.finance.FinancialGoals;
import fds.radar.entity.user.Users;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SimulatedSubscriptions {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long simulatedSubscriptionId;
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=false)
    private Users user;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="product_id", nullable=false)
    private FinancialProducts product;

    // 이 상품 대금이 실제로 빠져나가는 계좌
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="account_id", nullable=false)
    private Accounts account;

    // 이 가입이 기여하는 재무목표(선택사항 - 안 고르면 null)
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="goal_id", nullable=true)
    private FinancialGoals goal;

    private Long subscriptionAmount;
    private Long monthlyPayment;
    private Integer subscriptionPeriod;
    private Long expectedMaturityAmount;

    // 실제로 계좌에서 빠져나가 누적된 금액(일시납은 가입 즉시 전액, 적금은 회차마다 누적)
    @Builder.Default
    private Long paidAmount = 0L;

    // 적금(월납) 상품만 사용 - 몇 회차까지 납입했는지
    private Integer paidInstallments;

    // 적금(월납) 상품만 사용 - 다음 자동 납입 예정일. 완납/일시납이면 null
    private LocalDateTime nextPaymentDate;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private SubscriptionStatus subscriptionStatus = SubscriptionStatus.ACTIVE;

    private LocalDateTime subscribedAt;
    private LocalDateTime cancelledAt;
    private LocalDateTime completedAt;
}
