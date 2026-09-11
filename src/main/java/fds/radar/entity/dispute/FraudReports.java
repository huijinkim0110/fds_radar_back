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

    // [D파트 담당자 추가] 선택형 신고 사유 (본인이 하지 않은 거래 / 결제 금액이 다름 / 중복 결제 / 알 수 없는 가맹점 / 이의제기 통합분 4개 등)
    private String reasonCategory;

    @Column(columnDefinition = "TEXT")
    private String reportContent;

    @Enumerated(EnumType.STRING)
    private ReportStatus reportStatus;

    private LocalDateTime reportedAt;
    private LocalDateTime processedAt;
}