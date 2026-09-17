package fds.radar.repository.dispute;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import fds.radar.entity.dispute.DisputeRequests;

public interface DisputeRequestRepository extends JpaRepository<DisputeRequests, Long> {
    List<DisputeRequests> findByUser_UserId(Long userId);
    List<DisputeRequests> findByFraudReport_FraudReportId(Long fraudReportId);

    // 본인 소유 이의제기 검증용
    Optional<DisputeRequests> findByDisputeRequestIdAndUser_UserId(Long disputeRequestId, Long userId);
}
