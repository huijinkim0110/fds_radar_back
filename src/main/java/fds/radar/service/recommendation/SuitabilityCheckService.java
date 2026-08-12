package fds.radar.service.recommendation;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fds.radar.common.PreferredPeriod;
import fds.radar.common.RiskLevel;
import fds.radar.common.RiskTendency;
import fds.radar.common.SuitabilityResult;
import fds.radar.dto.recommendation.SuitabilityCheckRequestDTO;
import fds.radar.entity.finance.InvestmentProfiles;
import fds.radar.entity.financialProduct.FinancialProducts;
import fds.radar.entity.recommendation.SuitabilityChecks;
import fds.radar.entity.user.Users;
import fds.radar.repository.financialProduct.FinancialProductsRepository;
import fds.radar.repository.recommendation.SuitabilityChecksRepository;
import fds.radar.repository.user.UserRepository;
import fds.radar.service.finance.InvestmentProfileService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SuitabilityCheckService {
    
    private final SuitabilityChecksRepository suitabilityChecksRepository;
    private final FinancialProductsRepository financialProductsRepository;
    private final UserRepository userRepository;
    private final InvestmentProfileService investmentProfileService;

    // 투자성향별 허용 위험등급 매핑
    private static final Map<RiskTendency, Set<RiskLevel>> RISK_TENDENCY_MAP = Map.of(
        RiskTendency.STABLE, EnumSet.of(RiskLevel.VERY_LOW, RiskLevel.LOW),
        RiskTendency.NEUTRAL, EnumSet.of(RiskLevel.VERY_LOW, RiskLevel.LOW, RiskLevel.MEDIUM),
        RiskTendency.ACTIVE, EnumSet.of(RiskLevel.VERY_LOW, RiskLevel.LOW, RiskLevel.MEDIUM, RiskLevel.HIGH),
        RiskTendency.AGGRESSIVE, EnumSet.allOf(RiskLevel.class)
    );

    // 선호기간별 허용 최대 가입기간(개월), null이면 제한 없음
    private static final Map<PreferredPeriod, Integer> PERIOD_LIMIT_MAP = Map.of(
        PreferredPeriod.SHORT_TERM, 12,
        PreferredPeriod.MID_TERM, 36
        // LONG_TERM은 맵에 없음 -> 제한 없음 처리
    );

    // 상품 상세 페이지에서 적합성 검사 실행
    // - 투자성향 진단 이력이 없으면 InvestmentProfileService에서 예외 발생
    // - 위험등급 / 투자기간 / 원금보장 3가지만 검사(금액은 가입단곙서)
    @Transactional
    public SuitabilityChecks checkSuitability(SuitabilityCheckRequestDTO dto) {
        Users user = userRepository.findById(dto.getUserId())
                                   .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        FinancialProducts product = financialProductsRepository.findById(dto.getProductId())
                                                               .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        // 최신 투자성향 진단 결과 가져오기
        InvestmentProfiles profile = investmentProfileService.getLatestProfile(dto.getUserId());

        boolean riskMatch = checkRiskMatch(profile.getRiskTendency(), product.getRiskLevel());
        boolean periodMatch = checkPeriodMatch(profile.getPreferredPeriod(), product.getSubscriptionPeriod());
        boolean principalProtectionMatch = checkPrincipalProtectionMatch(profile.isPrincipalProtectionPreference(), product.isPrincipalProtection());
        boolean amountMatch = true; // 금액 검사는 가입 단계로 넘기기

        SuitabilityResult result = (riskMatch && periodMatch && principalProtectionMatch)
            ? SuitabilityResult.SUITABLE
            : SuitabilityResult.UNSUITABLE;
        
        String reason = buildCheckReason(riskMatch, periodMatch, principalProtectionMatch);

        SuitabilityChecks check = SuitabilityChecks.builder()
                                                   .user(user)
                                                   .product(product)
                                                   .riskMatch(riskMatch)
                                                   .periodMatch(periodMatch)
                                                   .amountMatch(amountMatch)
                                                   .principalProtectionMatch(principalProtectionMatch)
                                                   .suitabilityResult(result)
                                                   .checkReason(reason)
                                                   .checkedAt(LocalDateTime.now())
                                                   .build();

        return suitabilityChecksRepository.save(check);
    }

    // 검사 이력 조회(최신순)
    @Transactional(readOnly=true)
    public List<SuitabilityChecks> getCheckHistory(Long userId, Long productId) {
        return suitabilityChecksRepository.findByUser_UserIdAndProduct_ProductIdOrderByCheckedAtDesc(userId, productId);
    }

    private boolean checkRiskMatch(RiskTendency tendency, RiskLevel productRiskLevel) {
        return RISK_TENDENCY_MAP.get(tendency).contains(productRiskLevel);
    }

    private boolean checkPeriodMatch(PreferredPeriod preferredPeriod, Integer subscriptionPeriod) {
        Integer maxMonths = PERIOD_LIMIT_MAP.get(preferredPeriod); // LONG_TERM -> null(제한없음)
        if (maxMonths == null || subscriptionPeriod == null) {
            return true;
        }
        return subscriptionPeriod <= maxMonths;
    }

    private boolean checkPrincipalProtectionMatch(boolean userRequiresProtection, boolean productHasProtection) {
        // 사용자가 원금보장을 요구하지 않으면 상품 상태와 무관하게 항상 적합
        if (!userRequiresProtection) {
            return true;
        }
        return productHasProtection;
    }

    private String buildCheckReason(boolean riskMatch, boolean periodMatch, boolean principalProtectionMatch) {
        if (riskMatch && periodMatch && principalProtectionMatch) {
            return "투자성향, 투자기간, 원금보장 조건을 모두 충족합니다.";
        }

        StringBuilder sb = new StringBuilder("다음 항목에서 부적합 판정되었습니다: ");
        if (!riskMatch) sb.append("[위험등급 초과] ");
        if (!periodMatch) sb.append("[투자기간 초과] ");
        if (!principalProtectionMatch) sb.append("[원금보장 조건 미충족] ");
        return sb.toString().trim();
    }

}
