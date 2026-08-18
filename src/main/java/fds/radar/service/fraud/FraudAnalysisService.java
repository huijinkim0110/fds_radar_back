package fds.radar.service.fraud;

import java.util.List;

import org.springframework.stereotype.Service;

import fds.radar.common.FraudDecision;
import fds.radar.common.PredictedResult;
import fds.radar.dto.fraud.FraudCaseListResponse;
import fds.radar.entity.fraud.FraudCases;
import fds.radar.repository.fraud.FraudCaseRepository;
import lombok.RequiredArgsConstructor;

/**
 * 오탐(False Positive)·미탐(False Negative) 조회를 담당하는 서비스.
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
    // (원래는 A의 fraud_reports와 교차검증해야 더 정확하지만, 그쪽 데이터가
    //  아직 준비 안 됐을 수 있어 지금은 fraud_cases 자체 데이터만으로 판단)
    public List<FraudCaseListResponse> getFalseNegatives() {
        List<FraudCases> cases = fraudCaseRepository.findByDetectionResult_PredictedResultAndFraudDecision(
                PredictedResult.NORMAL, FraudDecision.FRAUD);
        return cases.stream().map(this::toListResponse).toList();
    }

    private FraudCaseListResponse toListResponse(FraudCases fraudCase) {
        return FraudCaseListResponse.builder()
                .fraudCaseId(fraudCase.getFraudCaseId())
                .transactionId(fraudCase.getTransaction().getTransactionId())
                .fraudProbability(fraudCase.getDetectionResult().getFraudProbability())
                .priority(fraudCase.getPriority())
                .caseStatus(fraudCase.getCaseStatus())
                .assignedAdminId(fraudCase.getAssignedAdminId().getUserId())
                .openedAt(fraudCase.getOpenedAt())
                .build();
    }
}