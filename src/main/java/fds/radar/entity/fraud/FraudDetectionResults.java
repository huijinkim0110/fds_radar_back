package fds.radar.entity.fraud;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import fds.radar.common.PredictedFraudType;
import fds.radar.common.PredictedResult;
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
public class FraudDetectionResults {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long detectionResultId;
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="transaction_id", nullable=false)
    private Transactions transaction;
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="model_id", nullable=false)
    private AiModels model;

    @Column(precision=5, scale=2)
    private BigDecimal fraudProbability;

    @Enumerated(EnumType.STRING)
    private PredictedResult predictedResult;

    @Enumerated(EnumType.STRING)
    private PredictedFraudType fraudType;

    @Column(columnDefinition = "TEXT")
    private String detectionReason;

    @Column(precision=5, scale=2)
    private BigDecimal userPatternScore;

    private LocalDateTime detectedAt;
    
}
