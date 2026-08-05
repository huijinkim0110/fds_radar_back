package fds.radar.entity.recommendation;

import java.time.LocalDateTime;

import fds.radar.common.SuitabilityResult;
import fds.radar.entity.financialProduct.FinancialProducts;
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
public class SuitabilityChecks {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long suitabilityCheckId;
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=false)
    private Users user;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="product_id", nullable=false)
    private FinancialProducts product;

    private boolean riskMatch;
    private boolean periodMatch;
    private boolean amountMatch;
    private boolean principalProtectionMatch;

    @Enumerated(EnumType.STRING)
    private SuitabilityResult suitabilityResult;
    @Column(columnDefinition = "TEXT")
    private String checkReason;
    private LocalDateTime checkedAt;

}
