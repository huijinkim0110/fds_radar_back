package fds.radar.dto.fraud;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 관리자 마이페이지 대시보드 응답: 배정받은 사건 수 + 오늘 접수된 사건 수 + 처리 현황 요약
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminDashboardResponse {
    private long assignedCaseCount;   // 나에게 배정된 사건 수 (진행 중, CLOSED 제외)
    private long todayReceivedCaseCount; // 오늘 접수된 사건 수 (배정자 무관, 전체 기준)

    // 처리 현황 요약: 나에게 배정된 사건을 상태별로 집계
    private long receivedCaseCount;
    private long investigatingCaseCount;
    private long closedCaseCount;
}