package fds.radar.entity.financialProduct;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

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
    @UniqueConstraint(columnNames = {"comparison_id", "product_id"})
})
public class ProductComparisonItems {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long comparisonItemId;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="comparison_id", nullable=false)
    private ProductComparisons comparison;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="product_id", nullable=false)
    private FinancialProducts product;

    @CreationTimestamp
    private LocalDateTime addedAt;
}
