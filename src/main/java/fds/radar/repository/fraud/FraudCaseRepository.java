package fds.radar.repository.fraud;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

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
    // CLOSED는 이미 끝난 사건이라 "지금 처리해야 할 업무량"에 안 들어가야 하므로 상태로 걸러냄.
    // 호출부에서 CaseStatus.CLOSED를 넘기면 됨.
    long countByAssignedAdminId_UserIdAndCaseStatusNot(Long adminId, CaseStatus caseStatus);

    // 관리자 마이페이지 대시보드: 처리 현황 요약용 — 상태별 개수 조회
    long countByAssignedAdminId_UserIdAndCaseStatus(Long adminId, CaseStatus caseStatus);
   
    // 관리자 마이페이지 대시보드: 오늘 접수된 사건 수 (배정자 무관, 전체 기준)
    // openedAt이 오늘 자정 이후인 사건 전체를 센다.
    long countByOpenedAtGreaterThanEqual(LocalDateTime openedAt);       
}