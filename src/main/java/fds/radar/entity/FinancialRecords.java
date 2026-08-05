package fds.radar.entity;

import java.time.LocalDateTime;

import fds.radar.common.FinancialCategory;
import fds.radar.common.RecordType;
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
public class FinancialRecords {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long financialRecordId;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=false)
    private Users user;

    @Enumerated(EnumType.STRING)
    private RecordType recordType;
    @Enumerated(EnumType.STRING)
    private FinancialCategory financialCategory;

    private Long amount;
    
    @Column(columnDefinition = "TEXT")
    private String description;

    private LocalDateTime occurredAt;
    private LocalDateTime createdAt;
    
}
