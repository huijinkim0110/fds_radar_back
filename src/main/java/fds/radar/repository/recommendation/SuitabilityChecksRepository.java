package fds.radar.repository.recommendation;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import fds.radar.entity.recommendation.SuitabilityChecks;

public interface SuitabilityChecksRepository extends JpaRepository<SuitabilityChecks, Long> {
    // 특정 사용자-상품 조합의 적합성 검사 이력을 최신순으로 전체 조회
    // - 재검사 가능, "검사 이력 조회" 기능에서 사용
    List<SuitabilityChecks> findByUser_UserIdAndProduct_ProductIdOrderByCheckedAtDesc(Long userId, Long productId);

}
