package fds.radar.service.fraud;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import fds.radar.common.CasePriority;
import fds.radar.common.CaseStatus;
import fds.radar.common.FraudDecision;
import fds.radar.common.PredictedFraudType;
import fds.radar.common.PredictedResult;
import fds.radar.dto.fraud.FraudAnalysisStatsResponse;
import fds.radar.dto.fraud.FraudCaseListResponse;
import fds.radar.entity.fraud.FraudCases;
import fds.radar.repository.fraud.FraudCaseRepository;
import lombok.RequiredArgsConstructor;

/**
 * 오탐(False Positive)·미탐(False Negative) 조회, 이상거래 분석 통계를 담당하는 서비스.
 * FraudCaseService와 분리한 이유: 사건 처리(생성·상태변경·판정)와
 * 성능 분석용 조회는 성격이 달라서 별도 서비스로 나눔.
 */
@Service
@RequiredArgsConstructor
public class FraudAnalysisService {

    private final FraudCaseRepository fraudCaseRepository;

    // 오탐: AI는 FRAUD로 예측했지만 관리자 최종판정은 NORMAL인 사건
    public List<FraudCaseListResponse> getFalsePositives() {
        List<FraudCases> cases = fraudCaseRepository.findByDetectionResult_PredictedResultAndFraudDecision(
                PredictedResult.FRAUD, FraudDecision.NORMAL);
        return cases.stream().map(this::toListResponse).toList();
    }

    // 미탐: AI는 NORMAL로 예측했지만 관리자 최종판정은 FRAUD로 확정된 사건
    public List<FraudCaseListResponse> getFalseNegatives() {
        List<FraudCases> cases = fraudCaseRepository.findByDetectionResult_PredictedResultAndFraudDecision(
                PredictedResult.NORMAL, FraudDecision.FRAUD);
        return cases.stream().map(this::toListResponse).toList();
    }

    // 관리자 이상거래 분석 화면(AdminFraudAnalysis.jsx)용 통계
    public FraudAnalysisStatsResponse getStats() {
        LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        long monthlyDetectionCount = fraudCaseRepository.countByOpenedAtGreaterThanEqual(monthStart);

        long blockedCount = fraudCaseRepository.countByCaseStatusAndFraudDecision(
                CaseStatus.CLOSED, FraudDecision.FRAUD);

        long falsePositiveCount = fraudCaseRepository.findByDetectionResult_PredictedResultAndFraudDecision(
                PredictedResult.FRAUD, FraudDecision.NORMAL).size();

        // 추가: 미탐 — getFalseNegatives()와 같은 조건, 개수만 필요해서 재사용
        long falseNegativeCount = fraudCaseRepository.findByDetectionResult_PredictedResultAndFraudDecision(
                PredictedResult.NORMAL, FraudDecision.FRAUD).size();

        BigDecimal avgProbability = fraudCaseRepository.findAverageFraudProbability();
        if (avgProbability == null) {
            avgProbability = BigDecimal.ZERO;
        }

        List<FraudAnalysisStatsResponse.DailyCount> daily = buildDailyCounts();

        List<FraudAnalysisStatsResponse.TypeCount> types = fraudCaseRepository.countByFraudType().stream()
                .map(row -> FraudAnalysisStatsResponse.TypeCount.builder()
                        .fraudType((PredictedFraudType) row[0])
                        .count((Long) row[1])
                        .build())
                .toList();

        List<FraudAnalysisStatsResponse.PriorityCount> risk = fraudCaseRepository.countByPriority().stream()
                .map(row -> FraudAnalysisStatsResponse.PriorityCount.builder()
                        .priority((CasePriority) row[0])
                        .count((Long) row[1])
                        .build())
                .toList();

        return FraudAnalysisStatsResponse.builder()
                .monthlyDetectionCount(monthlyDetectionCount)
                .blockedCount(blockedCount)
                .falsePositiveCount(falsePositiveCount)
                .falseNegativeCount(falseNegativeCount)
                .averageFraudProbability(avgProbability)
                .daily(daily)
                .types(types)
                .risk(risk)
                .build();
    }

    // 최근 7일 일별 접수 건수 (데이터 없는 날짜는 0건으로 채움)
    private List<FraudAnalysisStatsResponse.DailyCount> buildDailyCounts() {
        LocalDate sevenDaysAgo = LocalDate.now().minusDays(6);
        List<FraudCases> recentCases = fraudCaseRepository
                .findByOpenedAtGreaterThanEqualOrderByOpenedAtAsc(sevenDaysAgo.atStartOfDay());

        Map<LocalDate, Long> countByDate = recentCases.stream()
                .collect(Collectors.groupingBy(f -> f.getOpenedAt().toLocalDate(), Collectors.counting()));

        List<FraudAnalysisStatsResponse.DailyCount> result = new ArrayList<>();
        for (LocalDate d = sevenDaysAgo; !d.isAfter(LocalDate.now()); d = d.plusDays(1)) {
            result.add(FraudAnalysisStatsResponse.DailyCount.builder()
                    .date(d)
                    .count(countByDate.getOrDefault(d, 0L))
                    .build());
        }
        return result;
    }

    private FraudCaseListResponse toListResponse(FraudCases fraudCase) {
        return FraudCaseListResponse.builder()
                .fraudCaseId(fraudCase.getFraudCaseId())
                .transactionId(fraudCase.getTransaction().getTransactionId())
                .transactionType(fraudCase.getTransaction().getTransactionType())  // 추가
                .fraudProbability(fraudCase.getDetectionResult().getFraudProbability())
                .priority(fraudCase.getPriority())
                .caseStatus(fraudCase.getCaseStatus())
                .assignedAdminId(fraudCase.getAssignedAdminId().getUserId())
                .openedAt(fraudCase.getOpenedAt())
                .build();
    }
}