package fds.radar.entity.dispute;

import java.time.LocalDateTime;

import fds.radar.common.ReportStatus;
import fds.radar.common.ReportType;
import fds.radar.entity.fraud.FraudCases;
import fds.radar.entity.transaction.Transactions;
import fds.radar.entity.user.Users;
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
public class FraudReports {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long fraudReportId;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=false)
    private Users user;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="transaction_id", nullable=false)
    private Transactions transaction;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="fraud_case_id", nullable=false)
    private FraudCases fraudCase;

    @Enumerated(EnumType.STRING)
    private ReportType reportType;
    @Column(columnDefinition = "TEXT")
    private String reportContent;
    @Enumerated(EnumType.STRING)
    private ReportStatus reportStatus;

    private LocalDateTime reportedAt;
    private LocalDateTime processedAt;
}
