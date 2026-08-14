package fds.radar.repository.fraud;

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
}