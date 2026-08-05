package fds.radar.entity;


import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDateTime;

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
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Liabilities {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long liabilityId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Enumerated(EnumType.STRING)
    @Column(name = "liability_type", nullable = false)
    private LiabilityType liabilityType;

    // Enum 정의
    public enum LiabilityType {
        CREDIT_LOAN,
        MORTGAGE,
        AUTO_LOAN,
        STUDENT_LOAN,
        CARD_LOAN,
        OTHER_LIABILITY
    }

    // 대출 기관명
    @Column(nullable = false)
    private String lenderName;

    // 최초 대출 원금
    @Column(nullable = false)
    private Long originalAmount;

    // 남은 상환 금액
    @Column(nullable = false)
    private Long remainingAmount;

    // 이자율 (총 4자리 중 소수점 아래 2자리, 최대 99.99%)
    @Column(name = "interest_rate", precision = 4, scale = 2, nullable = false)
    private BigDecimal interestRate;
    
    // 월 상환액
    @Column(nullable = false)
    private Long monthlyPayment;

    // 만기일
    @Column(nullable = false)
    private Date maturityDate;

    // 부채 등록 시점 
    @Column(nullable = false)
    private LocalDateTime createdAt; 

}
