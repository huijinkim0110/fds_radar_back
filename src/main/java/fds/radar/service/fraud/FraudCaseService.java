package fds.radar.service.fraud;

import java.util.List;

import org.springframework.stereotype.Service;

import fds.radar.dto.fraud.FraudCaseAssignRequest;
import fds.radar.dto.fraud.FraudCaseDetailResponse;
import fds.radar.dto.fraud.FraudCaseListResponse;
import fds.radar.dto.fraud.FraudCaseStatusRequest;
import fds.radar.dto.fraud.FraudConfirmationRequest;
import fds.radar.dto.fraud.FraudDecisionRequest;
import fds.radar.repository.fraud.FraudCaseRepository;
import lombok.RequiredArgsConstructor;

/**
 * 이상거래 사건(FraudCase)의 조회, 상태변경, 담당자배정, 최종판정을 담당하는 서비스.
 * FraudDetectionService와 마찬가지로 구현체 교체 계획이 없어서 인터페이스 없이 클래스로 작성.
 */
@Service
@RequiredArgsConstructor
public class FraudCaseService {

    private final FraudCaseRepository fraudCaseRepository;
    private final FraudCaseHistoryService fraudCaseHistoryService;

    // 5차: 관리자 사건 목록 조회
    public List<FraudCaseListResponse> getCaseList() {
        throw new UnsupportedOperationException("5차에서 구현 예정");
    }

    // 5차: 관리자 사건 상세 조회 (거래정보 + AI탐지결과 + 사건정보 + 사용자확인결과 조합)
    public FraudCaseDetailResponse getCaseDetail(Long fraudCaseId) {
        throw new UnsupportedOperationException("5차에서 구현 예정");
    }

    // 6차: 사건 상태 변경 (RECEIVED → INVESTIGATING → CLOSED)
    public void updateCaseStatus(Long fraudCaseId, FraudCaseStatusRequest request) {
        throw new UnsupportedOperationException("6차에서 구현 예정");
    }

    // 6차: 담당 관리자 배정
    public void assignAdmin(Long fraudCaseId, FraudCaseAssignRequest request) {
        throw new UnsupportedOperationException("6차에서 구현 예정");
    }

    // 6차: 사용자 본인거래 확인결과 반영
    public void updateConfirmation(Long fraudCaseId, FraudConfirmationRequest request) {
        throw new UnsupportedOperationException("6차에서 구현 예정");
    }

    // 9차: 최종 판정(정상/사기) + 사건 종결 처리
    public void finalizeDecision(Long fraudCaseId, FraudDecisionRequest request) {
        throw new UnsupportedOperationException("9차에서 구현 예정");
    }
}