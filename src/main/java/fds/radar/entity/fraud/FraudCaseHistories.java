package fds.radar.entity.fraud;

import java.time.LocalDateTime;

import fds.radar.common.CaseStatus;
import fds.radar.common.FraudActionType;
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
public class FraudCaseHistories {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long caseHistoryId;
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="fraud_case_id", nullable=false)
    private FraudCases fraudCase;

    @Enumerated(EnumType.STRING)
    private FraudActionType actionType;
    @Enumerated(EnumType.STRING)
    private CaseStatus previousStatus;
    @Enumerated(EnumType.STRING)
    private CaseStatus changedStatus;
    @Column(columnDefinition = "TEXT")
    private String actionContent;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="admin_id", nullable=false)
    private Users adminId;

    private LocalDateTime createdAt;

}
