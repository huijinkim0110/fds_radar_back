package fds.radar.entity.recommendation;

import fds.radar.entity.financialProduct.FinancialProducts;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = {"recommendation_result_id", "product_id"})
})
public class RecommendationItems {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long recommendationItemId;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="recommendation_result_id", nullable=false)
    private RecommendationResults recommendationResult;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="product_id", nullable=false)
    private FinancialProducts product;

    private Integer ranking;
    private Integer suitabilityScore;
    @Column(columnDefinition="TEXT")
    private String recommendationReason;
    private String warningMessage;
    
    private boolean excluded;
    private String exclusionReason;

}
