package fds.radar.service.recommendation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fds.radar.common.GoalStatus;
import fds.radar.dto.recommendation.AiRecommendationItemDTO;
import fds.radar.dto.recommendation.AiRecommendationResponseDTO;
import fds.radar.dto.recommendation.RecommendationRequestDTO;
import fds.radar.dto.recommendation.RecommendedProductDTO;
import fds.radar.dto.recommendation.UnifiedRecommendationResponseDTO;
import fds.radar.entity.finance.FinancialGoals;
import fds.radar.entity.finance.InvestmentProfiles;
import fds.radar.entity.financialProduct.FinancialProducts;
import fds.radar.entity.recommendation.RecommendationItems;
import fds.radar.entity.recommendation.RecommendationResults;
import fds.radar.entity.user.Users;
import fds.radar.repository.finance.FinancialGoalsRepository;
import fds.radar.repository.financialProduct.FinancialProductsRepository;
import fds.radar.repository.recommendation.RecommendationItemsRepository;
import fds.radar.repository.recommendation.RecommendationResultsRepository;
import fds.radar.repository.user.UserRepository;
import fds.radar.service.finance.InvestmentProfileService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UnifiedRecommendationService {
    
    private final AiRecommendationService aiRecommendationService;
    private final RuleBasedRecommendationService ruleBasedRecommendationService;
    private final InvestmentProfileService investmentProfileService;
    private final FinancialProductsRepository financialProductsRepository;
    private final FinancialGoalsRepository financialGoalsRepository;
    private final RecommendationResultsRepository recommendationResultsRepository;
    private final RecommendationItemsRepository recommendationItemsRepository;
    private final UserRepository userRepository;

    // 증권 AI + 보험 AI + 예적금 규칙기반을 합쳐서 통합 추천 리스트 생성
    @Transactional
    public UnifiedRecommendationResponseDTO getUnifiedRecommendations(RecommendationRequestDTO dto) {
        InvestmentProfiles profile = investmentProfileService.getLatestProfile(dto.getUserId());

        // 진행중(IN_PROGRESS) 목표 중 가장 최근 것 자동 선택, 없으면 null
        FinancialGoals goal = financialGoalsRepository.findFirstByUser_UserIdAndGoalStatusOrderByCreatedAtDesc(dto.getUserId(), GoalStatus.IN_PROGRESS)
                                                      .orElse(null);
        boolean goalMissing = (goal == null);
        List<RecommendedProductDTO> allResults = new ArrayList<>();

        // 1. 증권 AI 추천
        AiRecommendationResponseDTO securities = aiRecommendationService.getSecuritiesRecommendation(dto);
        allResults.addAll(mapAiResultsToProducts(securities, "AI_SECURITIES", goal));

        // 2. 보험 AI 추천
        AiRecommendationResponseDTO insurance = aiRecommendationService.getInsuranceRecommendation(dto);
        allResults.addAll(mapAiResultsToProducts(insurance, "AI_INSURANCE", goal));

        // 3. 예적금 규칙기반 추천
        allResults.addAll(ruleBasedRecommendationService.recommendDepositsAndSavings(profile.getRiskTendency(), goal));

        // 4. 중복 상품 제거(같은 productId면 점수 높은 것만 남김)
        List<RecommendedProductDTO> deduplicated = deduplicateByProductId(allResults);

        // 5. 점수 내림차순 정렬
        deduplicated.sort(Comparator.comparing(RecommendedProductDTO::getScore).reversed());

        // 6. 전체 통합 상위 5개만 반환
        List<RecommendedProductDTO> topFive = deduplicated.stream().limit(5).toList();

        // 7. 이력 저장
        RecommendationResults saved = saveHistory(dto.getUserId(), profile, goal, topFive);

        return UnifiedRecommendationResponseDTO.builder()
                                               .recommendationResultId(saved.getRecommendationResultId())
                                               .results(topFive)
                                               .goalMissing(goalMissing)
                                               .requestedAt(saved.getRequestedAt())
                                               .build();
    }

    // 추천 결과를 이력으로 저장(마이페이지 재조회용)
    private RecommendationResults saveHistory(Long userId, InvestmentProfiles profile, FinancialGoals goal, List<RecommendedProductDTO> topFive) {
        Users user = userRepository.findById(userId)
                                   .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        RecommendationResults result = RecommendationResults.builder()
                                                            .user(user)
                                                            .goal(goal)
                                                            .investmentProfile(profile)
                                                            .requestedAt(LocalDateTime.now())
                                                            .completedAt(LocalDateTime.now())
                                                            .build();
        RecommendationResults savedResult = recommendationResultsRepository.save(result);

        int rank = 1;
        for (RecommendedProductDTO item : topFive) {
            FinancialProducts product = financialProductsRepository.findById(item.getProductId()).orElse(null);
            if (product == null) continue;

            RecommendationItems recItem = RecommendationItems.builder()
                                                             .recommendationResult(savedResult)
                                                             .product(product)
                                                             .ranking(rank++)
                                                             .suitabilityScore(item.getScore() != null ? item.getScore().intValue() : null)
                                                             .build();
            recommendationItemsRepository.save(recItem);
        }
        return savedResult;
    }

    // 마이페이지 - 저장된 추천 이력 목록 조회(최신순)
    @Transactional(readOnly=true)
    public List<RecommendationResults> getHistory(Long userId) {
        return recommendationResultsRepository.findByUser_UserIdOrderByRequestedAtDesc(userId);
    }

    // 특정 추천 이력에 포함된 상품 목록 조회
    @Transactional(readOnly=true)
    public List<RecommendationItems> getHistoryItems(Long recommendationResultId) {
        return recommendationItemsRepository.findByRecommendationResult_RecommendationResultIdOrderByRankingAsc(recommendationResultId);
    }

    // AI가 반환한 상품명을, 우리 DB의 실제 productId와 매칭
    // - 매칭 안 되는 상품(우리 DB에 없는 이름)은 결과에서 제외
    // - 재무목표가 있고, 목표 기간보다 상품 가입기간이 길면 10점 감점(제외는 아님)
    private List<RecommendedProductDTO> mapAiResultsToProducts(AiRecommendationResponseDTO aiResponse, String source, FinancialGoals goal) {
        List<RecommendedProductDTO> mapped = new ArrayList<>();

        Long goalRemainingMonths = (goal != null && goal.getTargetDate() != null)
            ? java.time.temporal.ChronoUnit.MONTHS.between(LocalDateTime.now(), goal.getTargetDate())
            : null;

        for (AiRecommendationItemDTO item : aiResponse.getRecommendations()) {
            Optional<FinancialProducts> matched = financialProductsRepository.findByProductName(item.getProduct_name());

            if (matched.isEmpty()) {
                continue; // 우리 DB에 없는 상품은 건너뜀
            }

            FinancialProducts product = matched.get();
            double score = item.getScore();

            if (goalRemainingMonths != null && product.getSubscriptionPeriod() != null
                    && product.getSubscriptionPeriod() > goalRemainingMonths) {
                score = Math.max(score - 10, 0);
            }

            mapped.add(RecommendedProductDTO.builder()
                                            .productId(product.getProductId())
                                            .productName(item.getProduct_name())
                                            .score(score)
                                            .source(source)
                                            .build());

        }

        return mapped;
    }

    // 같은 productId가 여러 소스에서 중복 추천된 경우, 가장 높은 점수 하나만 남김
    // - LinkedHashMap을 써서 처음 만난 순서를 최대한 유지하면서 점수 비교
    private List<RecommendedProductDTO> deduplicateByProductId(List<RecommendedProductDTO> results) {
        Map<Long, RecommendedProductDTO> uniqueByProductId = new LinkedHashMap<>();

        for (RecommendedProductDTO item : results) {
            RecommendedProductDTO existing = uniqueByProductId.get(item.getProductId());

            // 처음 만난 상품이거나, 지금 점수가 기존보다 높으면 갱신
            if (existing == null || item.getScore() > existing.getScore()) {
                uniqueByProductId.put(item.getProductId(), item);
            }
        }

        return new ArrayList<>(uniqueByProductId.values());
    }
}
