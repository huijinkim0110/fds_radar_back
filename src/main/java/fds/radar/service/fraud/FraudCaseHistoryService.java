package fds.radar.service.fraud;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import fds.radar.common.CaseStatus;
import fds.radar.common.FraudActionType;
import fds.radar.dto.fraud.FraudCaseHistoryResponse;
import fds.radar.entity.fraud.FraudCaseHistories;
import fds.radar.entity.fraud.FraudCases;
import fds.radar.entity.user.Users;
import fds.radar.repository.fraud.FraudCaseHistoryRepository;
import lombok.RequiredArgsConstructor;

/**
 * 사건 처리 이력(FraudCaseHistories)을 자동으로 기록하고 조회하는 서비스.
 *
 * 중요: 관리자가 직접 이력을 입력하는 게 아니라,
 * FraudCaseService 등 다른 서비스에서 상태 변경이 일어날 때마다
 * record()를 호출해서 자동으로 남기는 구조 (7차 목표 핵심).
 */
@Service
@RequiredArgsConstructor
public class FraudCaseHistoryService {

    private final FraudCaseHistoryRepository fraudCaseHistoryRepository;

    // 사건에 어떤 조치가 있을 때마다 호출해서 이력을 자동 기록
    public void record(FraudCases fraudCase, FraudActionType actionType,
                        CaseStatus previousStatus, CaseStatus changedStatus,
                        String actionContent, Long adminId) {

        FraudCaseHistories history = FraudCaseHistories.builder()
                .fraudCase(fraudCase)
                .actionType(actionType)
                .previousStatus(previousStatus)
                .changedStatus(changedStatus)
                .actionContent(actionContent)
                .adminId(Users.builder().userId(adminId).build())
                .createdAt(LocalDateTime.now())
                .build();

        fraudCaseHistoryRepository.save(history);
    }

    // 특정 사건의 이력을 시간순으로 조회
    public List<FraudCaseHistoryResponse> getHistories(Long fraudCaseId) {
        return fraudCaseHistoryRepository
                .findByFraudCase_FraudCaseIdOrderByCreatedAtAsc(fraudCaseId)
                .stream()
                .map(h -> FraudCaseHistoryResponse.builder()
                        .caseHistoryId(h.getCaseHistoryId())
                        .fraudCaseId(h.getFraudCase().getFraudCaseId())
                        .actionType(h.getActionType())
                        .previousStatus(h.getPreviousStatus())
                        .changedStatus(h.getChangedStatus())
                        .actionContent(h.getActionContent())
                        .adminId(h.getAdminId() != null ? h.getAdminId().getUserId() : null)
                        .createdAt(h.getCreatedAt())
                        .build())
                .toList();
    }
}