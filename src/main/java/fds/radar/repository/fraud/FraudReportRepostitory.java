package fds.radar.repository.fraud;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import fds.radar.entity.dispute.FraudReports;

public interface FraudReportRepostitory extends JpaRepository<FraudReports, Long> {
    List<FraudReports> findByUser_UserId(Long userId);
    List<FraudReports> findByStatus(String status);
}
