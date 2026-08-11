package fds.radar.repository.recommendation;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import fds.radar.entity.recommendation.RecommendationFeedbacks;

public interface RecommendationFeedbacksRepository extends JpaRepository<RecommendationFeedbacks, Long> {
    // 특정 추천 항목에 대해 사용자가 이미 남긴 피드백이 있는지 조회(중복 허용 x)
    Optional<RecommendationFeedbacks> findByUser_UserIdAndRecommendationItem_RecommendationItemId(Long userId, Long recommendationItemId);
}
