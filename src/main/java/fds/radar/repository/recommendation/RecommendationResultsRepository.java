package fds.radar.repository.recommendation;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import fds.radar.entity.recommendation.RecommendationResults;

public interface RecommendationResultsRepository extends JpaRepository<RecommendationResults, Long> {
    // 마이페이지 - 추천 이력 최신순 조회
    List<RecommendationResults> findByUser_UserIdOrderByRequestedAtDesc(Long userId);
}
