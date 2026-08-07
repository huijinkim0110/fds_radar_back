package fds.radar.entity.financialProduct;

import java.time.LocalDateTime;

import fds.radar.entity.user.Users;
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
    @UniqueConstraint(columnNames = {"user_id", "product_id"})
})
public class FavoriteProducts {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long favoriteProductId;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id")
    private Users user;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="product_id")
    private FinancialProducts product;

    private LocalDateTime regDate;
    
}
