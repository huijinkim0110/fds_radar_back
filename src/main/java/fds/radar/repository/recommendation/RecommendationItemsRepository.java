package fds.radar.repository.recommendation;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import fds.radar.entity.recommendation.RecommendationItems;

public interface RecommendationItemsRepository extends JpaRepository<RecommendationItems, Long> {
    // 특정 추천 이력에 속한 상품들을 순위 순으로 조회
    List<RecommendationItems> findByRecommendationResult_RecommendationResultIdOrderByRankingAsc(Long recommendationResultId);
}
