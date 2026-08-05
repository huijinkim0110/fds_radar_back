package fds.radar.entity.financialProduct;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import fds.radar.common.ProductStatus;
import fds.radar.common.ProductType;
import fds.radar.common.RiskLevel;
import fds.radar.entity.account.Institutions;
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
public class FinancialProducts {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long productId;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="institution_id", nullable=false)
    private Institutions institution;

    @Column(nullable=false)
    private String productName;
    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private ProductType productType;
    private String description;
    @Enumerated(EnumType.STRING)
    private RiskLevel riskLevel;

    private boolean principalProtection;
    private Long minAmount;
    private Long maxAmount;
    private Integer subscriptionPeriod;
    private Integer recommendedPeriod;
    @Column(precision=5, scale=2)
    private BigDecimal expectedReturnRate;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable=false)
    private ProductStatus productStatus = ProductStatus.ON_SALE;

    private LocalDateTime saleStartDate;
    private LocalDateTime saleEndDate;

}
