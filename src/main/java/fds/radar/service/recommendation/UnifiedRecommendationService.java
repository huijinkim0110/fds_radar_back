package fds.radar.service.recommendation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import fds.radar.dto.recommendation.AiRecommendationItemDTO;
import fds.radar.dto.recommendation.AiRecommendationResponseDTO;
import fds.radar.dto.recommendation.RecommendationRequestDTO;
import fds.radar.dto.recommendation.RecommendedProductDTO;
import fds.radar.entity.finance.InvestmentProfiles;
import fds.radar.entity.financialProduct.FinancialProducts;
import fds.radar.repository.financialProduct.FinancialProductsRepository;
import fds.radar.service.finance.InvestmentProfileService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UnifiedRecommendationService {
    
    private final AiRecommendationService aiRecommendationService;
    private final RuleBasedRecommendationService ruleBasedRecommendationService;
    private final InvestmentProfileService investmentProfileService;
    private final FinancialProductsRepository financialProductsRepository;

    // 증권 AI + 보험 AI + 예적금 규칙기반을 합쳐서 통합 추천 리스트 생성
    public List<RecommendedProductDTO> getUnifiedRecommendations(RecommendationRequestDTO dto) {
        InvestmentProfiles profile = investmentProfileService.getLatestProfile(dto.getUserId());

        List<RecommendedProductDTO> allResults = new ArrayList<>();

        // 1. 증권 AI 추천
        AiRecommendationResponseDTO securities = aiRecommendationService.getSecuritiesRecommendation(dto);
        allResults.addAll(mapAiResultsToProducts(securities, "AI_SECURITIES"));

        // 2. 보험 AI 추천
        AiRecommendationResponseDTO insurance = aiRecommendationService.getInsuranceRecommendation(dto);
        allResults.addAll(mapAiResultsToProducts(insurance, "AI_INSURANCE"));

        // 3. 예적금 규칙기반 추천
        allResults.addAll(ruleBasedRecommendationService.recommendDepositsAndSavings(profile.getRiskTendency()));

        // 점수 내림차순 정렬
        allResults.sort(Comparator.comparing(RecommendedProductDTO::getScore).reversed());

        return allResults;

    }

    // AI가 반환한 상품명을, 우리 DB의 실제 productId와 매칭
    // - 매칭 안 되는 상품(우리 DB에 없는 이름)은 결과에서 제외
    private List<RecommendedProductDTO> mapAiResultsToProducts(AiRecommendationResponseDTO aiResponse, String source) {
        List<RecommendedProductDTO> mapped = new ArrayList<>();

        for (AiRecommendationItemDTO item : aiResponse.getRecommendations()) {
            Optional<FinancialProducts> matched = financialProductsRepository.findByProductName(item.getProduct_name());

            if (matched.isEmpty()) {
                continue; // 우리 DB에 없는 상품은 건너뜀
            }

            mapped.add(RecommendedProductDTO.builder()
                                            .productId(matched.get().getProductId())
                                            .productName(item.getProduct_name())
                                            .score(item.getScore())
                                            .source(source)
                                            .build());

        }

        return mapped;
    }
}
