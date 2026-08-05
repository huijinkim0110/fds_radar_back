package fds.radar.entity.fraud;

import java.time.LocalDateTime;

import fds.radar.common.CasePriority;
import fds.radar.common.CaseStatus;
import fds.radar.common.FraudDecision;
import fds.radar.common.UserConfirmation;
import fds.radar.entity.Users;
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
public class FraudCases {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long fraudCaseId;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=false)
    private Users user;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="transaction_id", nullable=false)
    private Transactions transaction;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="detection_result_id", nullable=false)
    private FraudDetectionResults detectionResult;

    @Enumerated(EnumType.STRING)
    private CaseStatus caseStatus;
    @Enumerated(EnumType.STRING)
    private CasePriority priority;
    @Enumerated(EnumType.STRING)
    private UserConfirmation confirmation;
    @Enumerated(EnumType.STRING)
    private FraudDecision fraudDecision;

    // 배정된 담당 관리자
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="assigned_admin_id", nullable=false)
    private Users assignedAdminId;

    private LocalDateTime openedAt;
    private LocalDateTime closedAt;
}
