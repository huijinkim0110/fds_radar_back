package fds.radar.entity.dispute;

import java.time.LocalDateTime;

import fds.radar.common.RequestStatus;
import fds.radar.entity.Users;
import fds.radar.entity.transaction.Transactions;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DisputeRequests {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long disputeRequestId;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=false)
    private Users user;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="transaction_id", nullable=false)
    private Transactions transaction;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="fraud_report_id", nullable=false)
    private FraudReports fraudReport;

    @Column(columnDefinition = "TEXT")
    private String requestReason;
    private Integer requestAmount;

    @Enumerated(EnumType.STRING)
    private RequestStatus requestStatus;

    @Column(columnDefinition = "TEXT")
    private String adminResponse;
    private LocalDateTime requestedAt;
    private LocalDateTime processedAt;
}
