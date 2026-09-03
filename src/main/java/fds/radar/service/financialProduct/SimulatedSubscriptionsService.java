package fds.radar.service.financialProduct;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fds.radar.common.AccountStatus;
import fds.radar.common.GoalStatus;
import fds.radar.common.ProductType;
import fds.radar.common.SubscriptionStatus;
import fds.radar.dto.financialProduct.SubscriptionRequestDTO;
import fds.radar.dto.financialProduct.SubscriptionResponseDTO;
import fds.radar.entity.account.Accounts;
import fds.radar.entity.finance.FinancialGoals;
import fds.radar.entity.financialProduct.FinancialProducts;
import fds.radar.entity.financialProduct.SimulatedSubscriptions;
import fds.radar.entity.user.Users;
import fds.radar.exception.BusinessException;
import fds.radar.exception.NotFoundException;
import fds.radar.repository.account.AccountRepository;
import fds.radar.repository.finance.FinancialGoalsRepository;
import fds.radar.repository.financialProduct.FinancialProductsRepository;
import fds.radar.repository.financialProduct.SimulatedSubscriptionsRepository;
import fds.radar.repository.user.UserRepository;
import fds.radar.service.finance.FinancialGoalsService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SimulatedSubscriptionsService {

    private final SimulatedSubscriptionsRepository simulatedSubscriptionsRepository;
    private final FinancialProductsRepository financialProductsRepository;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final FinancialGoalsRepository financialGoalsRepository;
    private final FinancialGoalsService financialGoalsService;

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

        Accounts owned = accountRepository.findByAccountIdAndUser_UserId(dto.getAccountId(), dto.getUserId())
                .orElseThrow(() -> new NotFoundException("계좌를 찾을 수 없습니다."));

        if (owned.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new BusinessException("사용할 수 없는 계좌입니다.");
        }

        FinancialGoals goal = null;
        if (dto.getGoalId() != null) {
            goal = financialGoalsRepository.findByGoalIdAndUser_UserId(dto.getGoalId(), dto.getUserId())
                    .orElseThrow(() -> new NotFoundException("목표를 찾을 수 없습니다."));
            if (goal.getGoalStatus() != GoalStatus.IN_PROGRESS) {
                throw new BusinessException("진행중인 목표만 연결할 수 있습니다.");
            }
        }

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

        // 가입 시점 첫 납입 - 일시납은 전액, 적금은 1회차 월납입액만 실제 차감
        long firstPaymentAmount = isInstallment ? monthlyPayment : dto.getSubscriptionAmount();

        Accounts lockedAccount = accountRepository.findByAccountIdForUpdate(owned.getAccountId())
                .orElseThrow(() -> new NotFoundException("계좌를 찾을 수 없습니다."));

        BigDecimal paymentAmount = BigDecimal.valueOf(firstPaymentAmount);
        if (lockedAccount.getBalance().compareTo(paymentAmount) < 0) {
            throw new BusinessException("계좌 잔액이 부족합니다.");
        }
        lockedAccount.setBalance(lockedAccount.getBalance().subtract(paymentAmount));

        LocalDateTime now = LocalDateTime.now();
        boolean hasMorePayments = isInstallment && dto.getSubscriptionPeriod() > 1;

        SimulatedSubscriptions subscription = SimulatedSubscriptions.builder()
                .user(user)
                .product(product)
                .account(lockedAccount)
                .goal(goal)
                .subscriptionAmount(dto.getSubscriptionAmount())
                .monthlyPayment(monthlyPayment)
                .subscriptionPeriod(dto.getSubscriptionPeriod())
                .expectedMaturityAmount(expectedMaturityAmount)
                .paidAmount(firstPaymentAmount)
                .paidInstallments(isInstallment ? 1 : null)
                .nextPaymentDate(hasMorePayments ? now.plusMonths(1) : null)
                .subscriptionStatus(SubscriptionStatus.ACTIVE)
                .subscribedAt(now)
                .build();

        SimulatedSubscriptions saved = simulatedSubscriptionsRepository.save(subscription);

        if (goal != null) {
            financialGoalsService.adjustCurrentAmount(goal.getGoalId(), firstPaymentAmount);
        }

        return toResponseDTO(saved);
    }

    @Transactional
    public void cancel(Long simulatedSubscriptionId) {
        SimulatedSubscriptions subscription = simulatedSubscriptionsRepository.findById(simulatedSubscriptionId)
                .orElseThrow(() -> new IllegalArgumentException("가입 내역을 찾을 수 없습니다."));

        if (subscription.getSubscriptionStatus() != SubscriptionStatus.ACTIVE) {
            throw new BusinessException("이미 종료된 가입 건입니다.");
        }

        boolean isInstallment = subscription.getMonthlyPayment() != null;
        FinancialProducts product = subscription.getProduct();

        long refundAmount;
        if (isInstallment) {
            int paidInstallments = subscription.getPaidInstallments() != null ? subscription.getPaidInstallments() : 0;
            refundAmount = calculateInstallmentMaturity(subscription.getMonthlyPayment(),
                    product.getExpectedReturnRate(), paidInstallments);
        } else {
            int elapsedMonths = (int) Math.max(0,
                    ChronoUnit.MONTHS.between(subscription.getSubscribedAt(), LocalDateTime.now()));
            refundAmount = calculateLumpSumMaturity(subscription.getSubscriptionAmount(),
                    product.getExpectedReturnRate(), elapsedMonths);
        }

        if (refundAmount > 0) {
            Accounts account = accountRepository.findByAccountIdForUpdate(subscription.getAccount().getAccountId())
                    .orElseThrow(() -> new NotFoundException("계좌를 찾을 수 없습니다."));
            account.setBalance(account.getBalance().add(BigDecimal.valueOf(refundAmount)));

            if (subscription.getGoal() != null) {
                long principalPortion = subscription.getPaidAmount() != null ? subscription.getPaidAmount() : 0L;
                financialGoalsService.adjustCurrentAmount(subscription.getGoal().getGoalId(), -principalPortion);
            }
        }

        subscription.setSubscriptionStatus(SubscriptionStatus.CANCELLED);
        subscription.setCancelledAt(LocalDateTime.now());
        subscription.setNextPaymentDate(null);
    }

    @Transactional(readOnly = true)
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

    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void processDuePayments() {
        LocalDateTime now = LocalDateTime.now();
        List<SimulatedSubscriptions> activeSubs = simulatedSubscriptionsRepository
                .findBySubscriptionStatus(SubscriptionStatus.ACTIVE);

        for (SimulatedSubscriptions s : activeSubs) {
            boolean isInstallment = s.getMonthlyPayment() != null;

            if (isInstallment) {
                processInstallmentPayment(s, now);
            } else if (!s.getSubscribedAt().plusMonths(s.getSubscriptionPeriod()).isAfter(now)) {
                Accounts account = accountRepository.findByAccountIdForUpdate(s.getAccount().getAccountId())
                        .orElse(null);
                if (account != null) {
                    account.setBalance(account.getBalance().add(BigDecimal.valueOf(s.getExpectedMaturityAmount())));
                    s.setSubscriptionStatus(SubscriptionStatus.COMPLETED);
                    s.setCompletedAt(now);
                }
            }
        }
    }

    private void processInstallmentPayment(SimulatedSubscriptions s, LocalDateTime now) {
        if (s.getNextPaymentDate() == null || s.getNextPaymentDate().isAfter(now))
            return;

        int paidSoFar = s.getPaidInstallments() != null ? s.getPaidInstallments() : 0;
        if (paidSoFar >= s.getSubscriptionPeriod())
            return;

        Accounts account = accountRepository.findByAccountIdForUpdate(s.getAccount().getAccountId())
                .orElse(null);
        if (account == null)
            return;

        BigDecimal payment = BigDecimal.valueOf(s.getMonthlyPayment());
        if (account.getBalance().compareTo(payment) < 0) {
            return;
        }

        account.setBalance(account.getBalance().subtract(payment));
        s.setPaidAmount((s.getPaidAmount() != null ? s.getPaidAmount() : 0L) + s.getMonthlyPayment());

        if (s.getGoal() != null) {
            financialGoalsService.adjustCurrentAmount(s.getGoal().getGoalId(), s.getMonthlyPayment());
        }

        int paidCount = paidSoFar + 1;
        s.setPaidInstallments(paidCount);

        if (paidCount >= s.getSubscriptionPeriod()) {
            account.setBalance(account.getBalance().add(BigDecimal.valueOf(s.getExpectedMaturityAmount())));
            s.setSubscriptionStatus(SubscriptionStatus.COMPLETED);
            s.setCompletedAt(now);
            s.setNextPaymentDate(null);
        } else {
            s.setNextPaymentDate(s.getNextPaymentDate().plusMonths(1));
        }
    }

    // 일시납(예금 등) 단리 : 원금 * (1 + 연이자율 * 개월수/12)
    private Long calculateLumpSumMaturity(Long principal, BigDecimal expectedReturnRate, Integer periodMonths) {
        if (expectedReturnRate == null || periodMonths == null) {
            return principal;
        }

        BigDecimal rate = expectedReturnRate.divide(BigDecimal.valueOf(100));
        BigDecimal periodRatio = BigDecimal.valueOf(periodMonths).divide(BigDecimal.valueOf(12), 6,
                RoundingMode.HALF_UP);
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
        boolean isInstallment = s.getMonthlyPayment() != null;
        long target = isInstallment
                ? s.getMonthlyPayment() * s.getSubscriptionPeriod()
                : s.getSubscriptionAmount();
        long paid = s.getPaidAmount() != null ? s.getPaidAmount() : 0L;
        double achievementRate = target == 0 ? 0.0
                : BigDecimal.valueOf(paid * 100.0 / target).setScale(1, RoundingMode.HALF_UP).doubleValue();

        return SubscriptionResponseDTO.builder()
                .simulatedSubscriptionId(s.getSimulatedSubscriptionId())
                .productName(s.getProduct().getProductName())
                .accountId(s.getAccount().getAccountId())
                .accountNumber(maskAccountNumber(s.getAccount().getAccountNumber()))
                .goalId(s.getGoal() != null ? s.getGoal().getGoalId() : null)
                .goalName(s.getGoal() != null ? s.getGoal().getGoalName() : null)
                .subscriptionAmount(s.getSubscriptionAmount())
                .monthlyPayment(s.getMonthlyPayment())
                .subscriptionPeriod(s.getSubscriptionPeriod())
                .expectedMaturityAmount(s.getExpectedMaturityAmount())
                .paidAmount(paid)
                .paidInstallments(s.getPaidInstallments())
                .achievementRate(achievementRate)
                .nextPaymentDate(s.getNextPaymentDate())
                .subscriptionStatus(s.getSubscriptionStatus())
                .subscribedAt(s.getSubscribedAt())
                .completedAt(s.getCompletedAt())
                .build();
    }

    private String maskAccountNumber(String number) {
        if (number == null || number.length() < 4)
            return number;
        return "****" + number.substring(number.length() - 4);
    }

}
