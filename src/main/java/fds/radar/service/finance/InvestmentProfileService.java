package fds.radar.service.finance;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fds.radar.common.InvestmentExperience;
import fds.radar.common.LossTolerance;
import fds.radar.common.PreferredPeriod;
import fds.radar.common.RiskTendency;
import fds.radar.dto.finance.InvestmentDiagnosisRequestDTO;
import fds.radar.entity.finance.InvestmentProfiles;
import fds.radar.entity.user.Users;
import fds.radar.repository.finance.InvestmentProfilesRepository;
import fds.radar.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InvestmentProfileService {
    
    private final InvestmentProfilesRepository investmentProfilesRepository;
    private final UserRepository userRepository;

    // 설문 응답을 받아 점수 계산 후 투자성향 진단결과 저장
    // 진단은 여러 번 가능
    @Transactional
    public InvestmentProfiles diagnose(InvestmentDiagnosisRequestDTO dto) {
        Users user = userRepository.findById(dto.getUserId())
                                   .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        int totalScore = dto.getAgeScore()
                       + dto.getInvestmentExperienceScore()
                       + dto.getKnowledgeLevelScore()
                       + dto.getPreferredPeriodScore()
                       + dto.getAssetRatioScore()
                       + dto.getLossToleranceScore();

        RiskTendency riskTendency = calculateRiskTendency(totalScore);

        InvestmentProfiles profile = InvestmentProfiles.builder()
                                                       .user(user)
                                                       .riskTendency(riskTendency)
                                                       .investmentExperience(mapInvestmentExperience(dto.getInvestmentExperienceScore()))
                                                       .lossTolerance(mapLossTolerance(dto.getLossToleranceScore()))
                                                       .preferredPeriod(mapPreferredPeriod(dto.getPreferredPeriodScore()))
                                                       .principalProtectionPreference(dto.getPrincipalProtectionRequired())
                                                       .diagnosisScore(totalScore)
                                                       .diagnosedAt(LocalDateTime.now())
                                                       .build();

        return investmentProfilesRepository.save(profile);
    }

    // 마이페이지 화면용(최신 3건만 조회)
    @Transactional(readOnly=true)
    public List<InvestmentProfiles> getRecentProfiles(Long userId, int limit) {
        List<InvestmentProfiles> all = investmentProfilesRepository.findByUser_UserIdOrderByDiagnosedAtDesc(userId);

        return all.stream()
                  .limit(limit)
                  .collect(Collectors.toList());
    }

    // 금융상품 추천 입력값 및 적합성 검사용 - 가장 최근 진단 1건
    @Transactional(readOnly=true)
    public InvestmentProfiles getLatestProfile(Long userId) {
        List<InvestmentProfiles> all = investmentProfilesRepository.findByUser_UserIdOrderByDiagnosedAtDesc(userId);

        if (all.isEmpty()) {
            throw new IllegalStateException("투자성향 진단 이력이 없습니다. 진단을 먼저 받아주세요.");
        }
        return all.get(0);
    }

    // "진단 먼저 받기" 안내가 필요한지 판단할 때 사용
    @Transactional(readOnly=true)
    public boolean hasDiagnosisHistory(Long userId) {
        return investmentProfilesRepository.existsByUser_UserId(userId);
    }

    // 총점(0 ~ 16) -> 4단계 투자성향 매핑
    private RiskTendency calculateRiskTendency(int totalScore) {
        if (totalScore <= 4) return RiskTendency.STABLE;
        if (totalScore <= 9) return RiskTendency.NEUTRAL;
        if (totalScore <= 13) return RiskTendency.ACTIVE;
        return RiskTendency.AGGRESSIVE;
    }

    // 2번 문항 점수 (0 ~ 3) -> InvestmentExperience enum
    private InvestmentExperience mapInvestmentExperience(int score) {
        return switch (score) {
            case 0 -> InvestmentExperience.NONE;
            case 1 -> InvestmentExperience.BEGINNER;
            case 2 -> InvestmentExperience.INTERMEDIATE;
            default -> InvestmentExperience.EXPERIENCED;
        };
    }

    // 4번 문항 점수 (0 ~ 2) -> PreferredPeriod enum
    private PreferredPeriod mapPreferredPeriod(int score) {
        return switch (score) {
            case 0 -> PreferredPeriod.SHORT_TERM;
            case 1 -> PreferredPeriod.MID_TERM;
            default -> PreferredPeriod.LONG_TERM;
        };
    }

    // 6번 문항 점수 (0 ~ 3) -> LossTolerance enum
    private LossTolerance mapLossTolerance(int score) {
        return switch (score) {
            case 0 -> LossTolerance.NONE;
            case 1 -> LossTolerance.LOW;
            case 2 -> LossTolerance.MEDIUM;
            default -> LossTolerance.HIGH;
        };
    }

}
