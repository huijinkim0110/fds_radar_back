package fds.radar.repository.fraud;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import fds.radar.common.ReportStatus;
import fds.radar.entity.dispute.FraudReports;

public interface FraudReportRepostitory extends JpaRepository<FraudReports, Long> {
    List<FraudReports> findByUser_UserId(Long userId);
    List<FraudReports> findByReportStatus(ReportStatus status);

    // 본인 소유 신고 검증용
    Optional<FraudReports> findByFraudReportIdAndUser_UserId(Long reportId, Long userId);
}
