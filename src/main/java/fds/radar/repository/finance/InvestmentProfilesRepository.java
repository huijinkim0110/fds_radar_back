package fds.radar.repository.finance;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import fds.radar.entity.finance.InvestmentProfiles;

public interface InvestmentProfilesRepository extends JpaRepository<InvestmentProfiles, Long> {
    // 사용자의 투자성향 진단 이력을 최신순으로 전체 조회
    // - 추천 모델 입력값으로는 이 중 최신 1건만 사용(Service에서 .get(0) 처리)
    List<InvestmentProfiles> findByUser_UserIdOrderByDiagnosedAtDesc(Long userId);

    // "적합성 검사를 받기 위해서 진단 먼저" 안내 판단용
    boolean existsByUser_UserId(Long userId);
}
