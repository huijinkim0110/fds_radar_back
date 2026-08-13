package fds.radar.service.financialProduct;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fds.radar.common.ProductType;
import fds.radar.common.SubscriptionStatus;
import fds.radar.dto.financialProduct.SubscriptionRequestDTO;
import fds.radar.dto.financialProduct.SubscriptionResponseDTO;
import fds.radar.entity.financialProduct.FinancialProducts;
import fds.radar.entity.financialProduct.SimulatedSubscriptions;
import fds.radar.entity.user.Users;
import fds.radar.repository.financialProduct.FinancialProductsRepository;
import fds.radar.repository.financialProduct.SimulatedSubscriptionsRepository;
import fds.radar.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SimulatedSubscriptionsService {
    
    private final SimulatedSubscriptionsRepository simulatedSubscriptionsRepository;
    private final FinancialProductsRepository financialProductsRepository;
    private final UserRepository userRepository;

    // 상품 모의 가입
    // - SAVINGS(적금) : subscriptionAmount를 "월 납입액"으로 해석, 적금식 단리 계산
    // - 그 외(DEPOSIT 등) : subscriptionAmount를 "일시납 총액"으로 해석, 단리 계산
    @Transactional
    public SubscriptionResponseDTO subscribe(SubscriptionRequestDTO dto) {
        Users user = userRepository.findById(dto.getUserId())
                                   .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        FinancialProducts product = financialProductsRepository.findById(dto.getProductId())
                                                               .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        validateAmount(dto.getSubscriptionAmount(), product);

        boolean isInstallment = product.getProductType() == ProductType.SAVINGS;

        Long monthlyPayment;
        Long expectedMaturityAmount;

        if (isInstallment) {
            monthlyPayment = dto.getSubscriptionAmount();
            expectedMaturityAmount = calculateInstallmentMaturity(
                dto.getSubscriptionAmount(), product.getExpectedReturnRate(), dto.getSubscriptionPeriod());
        } else {
            monthlyPayment = null; // 일시납 상품은 월 납입 개념 없음
            expectedMaturityAmount = calculateLumpSumMaturity(
                dto.getSubscriptionAmount(), product.getExpectedReturnRate(), dto.getSubscriptionPeriod());
        }

        SimulatedSubscriptions subscription = SimulatedSubscriptions.builder()
                                                                    .user(user)
                                                                    .product(product)
                                                                    .subscriptionAmount(dto.getSubscriptionAmount())
                                                                    .monthlyPayment(monthlyPayment)
                                                                    .subscriptionPeriod(dto.getSubscriptionPeriod())
                                                                    .expectedMaturityAmount(expectedMaturityAmount)
                                                                    .subscriptionStatus(SubscriptionStatus.ACTIVE)
                                                                    .subscribedAt(LocalDateTime.now())
                                                                    .build();

        SimulatedSubscriptions saved = simulatedSubscriptionsRepository.save(subscription);
        return toResponseDTO(saved);
    }
    
    @Transactional
    public void cancel(Long simulatedSubscriptionId) {
        SimulatedSubscriptions subscription = simulatedSubscriptionsRepository.findById(simulatedSubscriptionId)
                                                                              .orElseThrow(() -> new IllegalArgumentException("가입 내역을 찾을 수 없습니다."));

        subscription.setSubscriptionStatus(SubscriptionStatus.CANCELLED);
        subscription.setCancelledAt(LocalDateTime.now());
    }

    @Transactional(readOnly=true)
    public List<SubscriptionResponseDTO> getPortfolio(Long userId) {
        List<SimulatedSubscriptions> subscriptions = simulatedSubscriptionsRepository
                .findByUser_UserIdAndSubscriptionStatus(userId, SubscriptionStatus.ACTIVE);

        return subscriptions.stream()
                            .map(this::toResponseDTO)
                            .toList();
    }

    private void validateAmount(Long amount, FinancialProducts product) {
        if (product.getMinAmount() != null && amount < product.getMinAmount()) {
            throw new IllegalArgumentException("최소 가입금액(" + product.getMinAmount() + "원) 미만입니다.");
        }
        if (product.getMaxAmount() != null && amount > product.getMaxAmount()) {
            throw new IllegalArgumentException("최대 가입금액(" + product.getMaxAmount() + "원)을 초과했습니다.");
        }
    }

    // 일시납(예금 등) 단리 : 원금 * (1 + 연이자율 * 개월수/12)
    private Long calculateLumpSumMaturity(Long principal, BigDecimal expectedReturnRate, Integer periodMonths) {
        if (expectedReturnRate == null || periodMonths == null) {
            return principal;
        }

        BigDecimal rate = expectedReturnRate.divide(BigDecimal.valueOf(100));
        BigDecimal periodRatio = BigDecimal.valueOf(periodMonths).divide(BigDecimal.valueOf(12), 6, RoundingMode.HALF_UP);
        BigDecimal multiplier = BigDecimal.ONE.add(rate.multiply(periodRatio));

        return BigDecimal.valueOf(principal).multiply(multiplier).setScale(0, RoundingMode.HALF_UP).longValue();
    }

    // 적금(SAVINGS) 단리 : 원금합 + 월납입액 * 연이자율/12 * 개월수*(개월수+1)/2
    private Long calculateInstallmentMaturity(Long monthlyAmount, BigDecimal expectedReturnRate, Integer periodMonths) {
        long totalPrincipal = monthlyAmount * periodMonths;

        if (expectedReturnRate == null || periodMonths == null) {
            return totalPrincipal;
        }

        BigDecimal monthlyRate = expectedReturnRate.divide(BigDecimal.valueOf(100))
                                                   .divide(BigDecimal.valueOf(12), 8, RoundingMode.HALF_UP);

        // n * (n+1) / 2
        BigDecimal sumOfMonths = BigDecimal.valueOf((long) periodMonths * (periodMonths + 1) / 2);

        BigDecimal interest = BigDecimal.valueOf(monthlyAmount)
                                        .multiply(monthlyRate)
                                        .multiply(sumOfMonths);

        return BigDecimal.valueOf(totalPrincipal).add(interest).setScale(0, RoundingMode.HALF_UP).longValue();
    }

    private SubscriptionResponseDTO toResponseDTO(SimulatedSubscriptions s) {
        return SubscriptionResponseDTO.builder()
                                      .simulatedSubscriptionId(s.getSimulatedSubscriptionId())
                                      .productName(s.getProduct().getProductName())
                                      .subscriptionAmount(s.getSubscriptionAmount())
                                      .monthlyPayment(s.getMonthlyPayment())
                                      .subscriptionPeriod(s.getSubscriptionPeriod())
                                      .expectedMaturityAmount(s.getExpectedMaturityAmount())
                                      .subscriptionStatus(s.getSubscriptionStatus())
                                      .subscribedAt(s.getSubscribedAt())
                                      .build();
    }
    
}
