package fds.radar.entity.dispute;

import java.time.LocalDateTime;

import fds.radar.common.LockRequestStatus;
import fds.radar.common.RequestTargetType;
import fds.radar.entity.fraud.FraudCases;
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
public class LockRequests {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long lockRequestId;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=false)
    private Users user;

    @Enumerated(EnumType.STRING)
    private RequestTargetType targetType;
    @Column(columnDefinition = "TEXT")
    private String requestReason;

    @Enumerated(EnumType.STRING)
    private LockRequestStatus requestStatus;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="fraud_case_id", nullable=true)
    private FraudCases fraudCase;

    @Column
    private Long targetId;

    private LocalDateTime requestedAt;
    private LocalDateTime processedAt;
    
}
