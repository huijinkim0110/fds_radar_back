package fds.radar.entity.dispute;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import fds.radar.common.ModelType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiModels {
 
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long modelId;

    @Column(nullable=false)
    private String modelName;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private ModelType modelType;

    @Column(nullable=false)
    private String version;

    @Column(nullable=false)
    private String algorithm;

    @Column(nullable=false)
    private String trainingDataName;

    @Column(nullable=false, precision=5, scale=4)
    private BigDecimal accuracy;
    
    @Column(nullable=false, precision=5, scale=4)
    private BigDecimal precisionScore;

    @Column(nullable=false, precision=5, scale=4)
    private BigDecimal recallScore;

    @Column(nullable=false, precision=5, scale=4)
    private BigDecimal f1Score;

    @Column(nullable=false)
    private String modelPath;

    @Column(nullable=false)
    private boolean active;

    @Column(nullable=false)
    private LocalDateTime trainedAt;
    
    @Column(nullable=false)
    private LocalDateTime registeredAt;
    
}
