package fds.radar.entity.recommendation;

import java.time.LocalDateTime;

import fds.radar.entity.Users;
import jakarta.persistence.Column;
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
    @UniqueConstraint(columnNames = {"user_id", "recommendation_item_id"})
})
public class RecommendationFeedbacks {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long feedbackId;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=false)
    private Users user;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="recommendation_item_id", nullable=false)
    private RecommendationItems recommendationItem;

    private Integer satisfactionScore;
    private boolean helpful;
    @Column(columnDefinition = "TEXT")
    private String feedbackContent;
    private LocalDateTime createdAt;

}
