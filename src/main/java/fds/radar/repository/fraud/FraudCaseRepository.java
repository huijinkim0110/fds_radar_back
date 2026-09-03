package fds.radar.repository.fraud;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import fds.radar.common.CaseStatus;
import fds.radar.common.FraudDecision;
import fds.radar.common.PredictedResult;
import fds.radar.entity.fraud.FraudCases;

public interface FraudCaseRepository extends JpaRepository<FraudCases, Long> {

    List<FraudCases> findByCaseStatus(CaseStatus caseStatus);

    Optional<FraudCases> findByTransaction_TransactionId(Long transactionId);

    Optional<FraudCases> findByDetectionResult_DetectionResultId(Long detectionResultId);

    List<FraudCases> findByAssignedAdminId_UserId(Long adminId);

    // 7차: 오탐 — AI는 FRAUD로 예측했지만 관리자 최종판정은 NORMAL인 사건
    List<FraudCases> findByDetectionResult_PredictedResultAndFraudDecision(
            PredictedResult predictedResult, FraudDecision fraudDecision);

    // 관리자 마이페이지 대시보드: 내가 배정받은 사건 수 중 아직 처리 중인 것만 (CLOSED 제외)
    long countByAssignedAdminId_UserIdAndCaseStatusNot(Long adminId, CaseStatus caseStatus);

    // 관리자 마이페이지 대시보드: 처리 현황 요약용 — 상태별 개수 조회
    long countByAssignedAdminId_UserIdAndCaseStatus(Long adminId, CaseStatus caseStatus);

    // 관리자 마이페이지 대시보드: 오늘 접수된 사건 수 (배정자 무관, 전체 기준)
    long countByOpenedAtGreaterThanEqual(LocalDateTime openedAt);

    // === 여기부터 신규: 이상거래 분석 통계용 (AdminFraudAnalysis.jsx 연동) ===

    // 종결 + 사기 확정된 사건 수 ("차단 처리" 건수)
    long countByCaseStatusAndFraudDecision(CaseStatus caseStatus, FraudDecision fraudDecision);

    // 전체 사건의 평균 AI 이상확률
    @Query("SELECT AVG(f.detectionResult.fraudProbability) FROM FraudCases f")
    java.math.BigDecimal findAverageFraudProbability();

    // 최근 N일 추이 계산용 — 날짜별 집계는 서비스 레이어에서 자바로 처리
    List<FraudCases> findByOpenedAtGreaterThanEqualOrderByOpenedAtAsc(LocalDateTime openedAt);

    // 이상거래 유형별 분포
    @Query("SELECT f.detectionResult.fraudType, COUNT(f) FROM FraudCases f " +
           "WHERE f.detectionResult.fraudType IS NOT NULL GROUP BY f.detectionResult.fraudType")
    List<Object[]> countByFraudType();

    // 위험도(priority)별 분포
    @Query("SELECT f.priority, COUNT(f) FROM FraudCases f GROUP BY f.priority")
    List<Object[]> countByPriority();
}