package fds.radar.entity.financialProduct;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
public class ProductRateHistories {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long rateHistoryId;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="product_id", nullable=false)
    private FinancialProducts product;

    @Column(precision=5, scale=2)
    private BigDecimal interestRate;
    @Column(precision=5, scale=2)
    private BigDecimal expectedReturnRate;

    private LocalDateTime effectiveStartDate;
    private LocalDateTime efectiveEndDate;
    private LocalDateTime createdAt;
}
