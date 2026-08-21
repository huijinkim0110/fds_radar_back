package fds.radar.service.fraud;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fds.radar.common.ReportStatus;
import fds.radar.common.ReportType;
import fds.radar.dto.fraud.FraudReportResponse;
import fds.radar.dto.fraud.FraudReportRequest;
import fds.radar.entity.dispute.FraudReports;
import fds.radar.entity.fraud.FraudCases;
import fds.radar.entity.transaction.Transactions;
import fds.radar.entity.user.Users;
import fds.radar.repository.fraud.FraudCaseRepository;
import fds.radar.repository.fraud.FraudReportRepostitory;
import fds.radar.repository.transaction.TransactionRepository;
import fds.radar.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FraudReportService {
    
    private final FraudReportRepostitory fraudReportRepostitory;
    private final FraudCaseRepository fraudCaseRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    // 피해 의심 거래 신고 접수
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

        FraudCases fraudCase = fraudCaseRepository
                .findByTransaction_TransactionId(
                        request.getTransactionId()
                )
                .orElseThrow(() -> 
                    new IllegalArgumentException(
                            "해당 거래의 이상거래 케이스를 찾을 수 업습니다."
                    )
            );

        FraudReports report = FraudReports.builder()
                .user(user)
                .transaction(transaction)
                .fraudCase(fraudCase)
                .reportType(ReportType.DAMAGE_REPORT)
                .reportContent(request.getReason())
                .reportStatus(ReportStatus.RECEIVED)
                .reportedAt(LocalDateTime.now())
                .build();

        fraudReportRepostitory.save(report);

        return toResponse(report);
    }
    
    // 사용자의 신고 목록 조회
    @Transactional(readOnly = true)
    public List<FraudReportResponse> getReports(Long userId) {

        return fraudReportRepostitory.findByUser_UserId(userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // 신고 한 건 조회
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

    // 관리자 신고 상태 변경
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

     // Entity -> Response
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

