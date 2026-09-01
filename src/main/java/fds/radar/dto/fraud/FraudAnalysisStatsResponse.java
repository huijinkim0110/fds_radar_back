package fds.radar.dto.fraud;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import fds.radar.common.CasePriority;
import fds.radar.common.PredictedFraudType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FraudAnalysisStatsResponse {
    private long monthlyDetectionCount;          // 이번 달 탐지 건수
    private long blockedCount;                   // 차단 처리 (종결 + 사기 확정)
    private long falsePositiveCount;              // 오탐(정상판정)
    private BigDecimal averageFraudProbability;   // 평균 AI점수

    private List<DailyCount> daily;   // 최근 7일 추이
    private List<TypeCount> types;    // 유형별 분포
    private List<PriorityCount> risk; // 위험도 비율

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DailyCount {
        private LocalDate date;
        private long count;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TypeCount {
        private PredictedFraudType fraudType;
        private long count;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PriorityCount {
        private CasePriority priority;
        private long count;
    }
}