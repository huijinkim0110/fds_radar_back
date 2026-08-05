package fds.radar.entity.transaction;

import java.time.LocalDateTime;

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
public class Merchants {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long merchantId;

    @Column(nullable = false)
    private String merchantName;

    @Enumerated(EnumType.STRING)
    @Column(name = "business_type", nullable = false)
    private BusinessType businessType;

    public enum BusinessType {
        RESTAURANT,
        MART,
        ONLINE_SHOPPING,
        TRANSPORTATION,
        MEDICAL,
        LEISURE,
        EDUCATION,
        OTHER_BUSINESS;
    }

    @Column(nullable = false)
    private String countryCode;

    @Column(nullable = false)
    private String region;

    @Column(nullable = false)
    private boolean onlineMerchant;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_status", nullable = false)
    private RiskStatus riskStatus;

    public enum RiskStatus {
        NORMAL,
        CAUTION,
        SUSPICIOUS;
    }

    @Column(nullable = false)
    private LocalDateTime regDate;
}
