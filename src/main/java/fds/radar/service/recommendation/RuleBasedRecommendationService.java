package fds.radar.service.recommendation;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import fds.radar.common.ProductType;
import fds.radar.common.RiskLevel;
import fds.radar.common.RiskTendency;
import fds.radar.dto.recommendation.RecommendedProductDTO;
import fds.radar.entity.finance.FinancialGoals;
import fds.radar.entity.financialProduct.FinancialProducts;
import fds.radar.repository.financialProduct.FinancialProductsRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RuleBasedRecommendationService {
    
    private final FinancialProductsRepository financialProductsRepository;

    // SuitabilityCheckService와 동일한 매핑표(투자성향별 허용 위험등급)
    private static final Map<RiskTendency, Set<RiskLevel>> RISK_TENDENCY_MAP = Map.of(
        RiskTendency.STABLE, EnumSet.of(RiskLevel.VERY_LOW, RiskLevel.LOW),
        RiskTendency.NEUTRAL, EnumSet.of(RiskLevel.VERY_LOW, RiskLevel.LOW, RiskLevel.MEDIUM),
        RiskTendency.ACTIVE, EnumSet.of(RiskLevel.VERY_LOW, RiskLevel.LOW, RiskLevel.MEDIUM, RiskLevel.HIGH),
        RiskTendency.AGGRESSIVE, EnumSet.allOf(RiskLevel.class)
    );

    // 예금/적금 상품을 금리순으로 정렬해 추천 목록 생성
    // - 투자성향에 안 맞는 위험등급은 제외 (예적금은 대부분 VERY_LOW라 사실상 거의 통과)
    // - 상위 항목일수록 높은 점수 부여(1등=100점 기준, 순위마다 5점씩 감소)
    // - 재무 목표가 있고, 목표 기간보다 상품 가입기간이 길면 10점 감점(제외는 아님)
    public List<RecommendedProductDTO> recommendDepositsAndSavings(RiskTendency riskTendency, FinancialGoals goal) {
        Set<RiskLevel> allowedLevels = RISK_TENDENCY_MAP.get(riskTendency);

        List<FinancialProducts> candidates = financialProductsRepository
            .findByProductTypeInAndRiskLevelIn(List.of(ProductType.DEPOSIT, ProductType.SAVINGS),
                                             allowedLevels);

        // 예상수익률(금리) 내림차순 정렬
        candidates.sort((a, b) -> b.getExpectedReturnRate().compareTo(a.getExpectedReturnRate()));

        Long goalRemainingMonths = (goal != null && goal.getTargetDate() != null)
            ? ChronoUnit.MONTHS.between(LocalDateTime.now(), goal.getTargetDate())
            : null;

        List<RecommendedProductDTO> result = new java.util.ArrayList<>();
        int rank = 0;
        for (FinancialProducts product : candidates) {
            double score = Math.max(100 - (rank * 5), 10); // 최소 10점까지만 감소

            if (goalRemainingMonths != null && product.getSubscriptionPeriod() != null
                    && product.getSubscriptionPeriod() > goalRemainingMonths) {
                score = Math.max(score - 10, 0);
            }
            
            result.add(RecommendedProductDTO.builder()
                                            .productId(product.getProductId())
                                            .productName(product.getProductName())
                                            .score(score)
                                            .source("RULE_BASED")
                                            .build());
            rank++;
        }

        return result;
    }
}
