package fds.radar.entity;

import java.time.LocalDateTime;

import fds.radar.common.IncomeSource;
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
public class FinancialProfiles {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long financialProfileId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Column(nullable = false)
    private String occupation;

    @Enumerated(EnumType.STRING)
    @Column( nullable = false)
    private IncomeSource incomeSource;

    

    @Column(nullable = false)
    private Long monthlyIncome;

    @Column(nullable = false)
    private Long monthlyExpenses;

    @Column(nullable = false)
    private Integer creditLevel;

    @Column(nullable = false)
    private Long availableMonthlyAmount;

    @Column(nullable = false)
    private Long emergencyFundAmount;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;
    

    
}
