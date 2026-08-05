package fds.radar.entity.financialProduct;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import fds.radar.entity.Users;
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
public class ProductComparisons {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long comparisonId;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id")
    private Users user;

    private String comparisonName;
    @CreationTimestamp
    private LocalDateTime createdAt;
}
