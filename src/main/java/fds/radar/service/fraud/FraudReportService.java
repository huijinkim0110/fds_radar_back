package fds.radar.service.fraud;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fds.radar.common.ReportStatus;
import fds.radar.common.ReportType;
import fds.radar.dto.fraud.FraudReportResponse;
import fds.radar.dto.fraud.FraudReportRequest;
import fds.radar.entity.dispute.FraudReports;
import fds.radar.entity.fraud.FraudCases;
import fds.radar.entity.fraud.FraudDetectionResults; // D파트 추가: 사후 신고 시 기존 탐지결과 재사용을 위해 추가
import fds.radar.entity.transaction.Transactions;
import fds.radar.entity.user.Users;
import fds.radar.repository.fraud.FraudCaseRepository;
import fds.radar.repository.fraud.FraudDetectionResultRepository; // D파트 추가: 거래의 최신 탐지결과 조회용으로 추가
import fds.radar.repository.fraud.FraudReportRepostitory;
import fds.radar.repository.transaction.TransactionRepository;
import fds.radar.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FraudReportService {

    private final FraudReportRepostitory fraudReportRepostitory;
    private final FraudCaseRepository fraudCaseRepository;
    private final FraudDetectionResultRepository fraudDetectionResultRepository; // D파트 추가: 사건이 없는 거래(AI 정상 판단)의 탐지결과 조회용
    private final FraudCaseService fraudCaseService; // D파트 추가: 사후 신고 시 사건을 새로 생성하기 위해 주입 (createCaseFromReport 호출)
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    // 피해 의심 거래 신고 접수
    // [D파트 수정] 미탐(false negative) 조회가 안 되던 문제 때문에 D파트(혜원)가 수정함.
    // 기존에는 해당 거래에 FraudCase가 이미 있는 경우만 처리 가능했음(없으면 예외로 실패).
    // 그런데 REQ-F-8-01(AI가 정상 판단한 거래도 사후 신고 가능)을 만족하려면
    // 사건이 아예 없는 거래(AI가 정상으로 판단해서 threshold 미만이라 자동 생성이 안 된 거래)도
    // 신고할 수 있어야 하고, 그래야 관리자가 나중에 FRAUD로 확정했을 때 미탐(false negative)으로
    // 잡힐 수 있음(FraudAnalysisService.getFalseNegatives()가 predictedResult=NORMAL &&
    // fraudDecision=FRAUD 조건으로 조회하는데, 사건 자체가 없으면 애초에 조회 대상이 될 수 없었음).
    // 그래서 사건 유무로 분기: 있으면 기존처럼 피해 신고(DAMAGE_REPORT)로 연결하고,
    // 없으면 사후 신고(POST_REPORT)로 보고 이 신고를 계기로 사건을 새로 생성함.
    @Transactional
    public FraudReportResponse create(
            Long userId,
            FraudReportRequest request) {

        Users user = userRepository.findById(userId)
                .orElseThrow(() ->
                    new IllegalArgumentException("사용자를 찾을 수 없습니다.")
            );

        Transactions transaction = transactionRepository
                .findByTransactionIdAndUser_UserId(
                        request.getTransactionId(),
                        userId
                )
                .orElseThrow(() ->
                    new IllegalArgumentException(
                            "해당 거래를 찾을 수 없습니다."
                    )
            );

        // [D파트 수정] orElseThrow로 바로 던지던 부분을 Optional로 받아서 있는지 없는지 먼저 분기하도록 변경
        Optional<FraudCases> existingCase = fraudCaseRepository
                .findByTransaction_TransactionId(request.getTransactionId());

        FraudCases fraudCase;
        ReportType reportType;

        if (existingCase.isPresent()) {
            // 기존 동작: 이미 AI가 이상거래로 판단해 사건이 있는 거래 -> 피해 신고
            fraudCase = existingCase.get();
            reportType = ReportType.DAMAGE_REPORT;
        } else {
            // [D파트 추가] 사건이 없는 거래 -> AI가 정상 판단했다는 뜻.
            // FraudDetectionResults는 AI 판정 시 결과와 무관하게 항상 저장되어 있으므로
            // (FraudDetectionService.detectFraud() 참고) 그걸 그대로 재사용해서 사건을 생성.
            FraudDetectionResults detectionResult = fraudDetectionResultRepository
                    .findTopByTransaction_TransactionIdOrderByDetectedAtDesc(request.getTransactionId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "해당 거래의 탐지 결과를 찾을 수 없습니다."
                    ));
            // [D파트 추가] FraudCaseService.createCaseIfNeeded()는 threshold 미만이면 아무것도
            // 생성하지 않으므로(정상 판단 거래는 정의상 threshold 미만) 그대로 못 씀.
            // D파트에서 threshold와 무관하게 무조건 생성/조회하는 createCaseFromReport()를
            // FraudCaseService에 새로 추가해서 사용.
            fraudCase = fraudCaseService.createCaseFromReport(detectionResult);
            reportType = ReportType.POST_REPORT;
        }

        FraudReports report = FraudReports.builder()
                .user(user)
                .transaction(transaction)
                .fraudCase(fraudCase)
                .reportType(reportType) // [D파트 수정] 하드코딩된 DAMAGE_REPORT 대신 위에서 분기한 값 사용
                .reportContent(request.getReason())
                .reportStatus(ReportStatus.RECEIVED)
                .reportedAt(LocalDateTime.now())
                .build();

        fraudReportRepostitory.save(report);

        return toResponse(report);
    }

    // 사용자의 신고 목록 조회 (변경 없음)
    @Transactional(readOnly = true)
    public List<FraudReportResponse> getReports(Long userId) {

        return fraudReportRepostitory.findByUser_UserId(userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // 신고 한 건 조회 (변경 없음)
    @Transactional(readOnly = true)
    public FraudReportResponse getReport(Long reportId) {
        FraudReports report = fraudReportRepostitory.findById(reportId)
                .orElseThrow(() ->
                    new IllegalArgumentException(
                            "신고 내역을 찾을 수 없습니다."
                    )
            );

        return toResponse(report);

    }

    // 관리자 신고 상태 변경 (변경 없음)
    @Transactional
    public FraudReportResponse updateStatus(
            Long reportId,
            ReportStatus status) {

        FraudReports report = fraudReportRepostitory.findById(reportId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "신고 내역을 찾을 수 없습니다."
                        )
            );

        report.setReportStatus(status);

        if (status == ReportStatus.PROCESSED) {
            report.setProcessedAt(LocalDateTime.now());
        }

        fraudReportRepostitory.save(report);

        return toResponse(report);
    }

     // Entity -> Response (변경 없음)
    private FraudReportResponse toResponse(
            FraudReports report) {

        return FraudReportResponse.builder()
                .id(report.getFraudReportId())
                .transactionId(
                        report.getTransaction().getTransactionId()
                )
                .reason(report.getReportContent())
                .status(report.getReportStatus().name())
                .createdAt(report.getReportedAt())
                .build();
    }
}