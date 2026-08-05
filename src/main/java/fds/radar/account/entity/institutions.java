package fds.radar.account.entity;



import java.time.LocalDateTime;

import fds.radar.account.common.InstitutionStatus;
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
public class institutions {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long institutionId;

    // 금융기관명 (국민은행, 신한카드 등)
    @Column(nullable = false)
    private String institutionName; 

    @Enumerated(EnumType.STRING)
    @Column(name = "institution_type", nullable = false)
    private InstitutionType institutionType;
    
    public enum InstitutionType{
        BANK,
        CARD_COMPANY,
        SECURITIES,
        INSURANCE,
        OTHER_INSTITURION;

    }

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private InstitutionStatus status = InstitutionStatus.ACTIVE; // 기본값 설정
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
}
