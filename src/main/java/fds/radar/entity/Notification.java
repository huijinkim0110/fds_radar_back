package fds.radar.entity;

import java.time.LocalDateTime;

import fds.radar.common.NotificationType;
import fds.radar.common.RelatedType;
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
public class Notification {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long notificationId;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=false)
    private Users user;

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    private String notificationTitle;
    @Column(columnDefinition = "TEXT")    
    private String notificationContent;
    @Enumerated(EnumType.STRING)
    private RelatedType relatedType;

    // PK값 어케 가져오지
    private Long relatedId;

    @Builder.Default
    private boolean readStatus = false;
    private LocalDateTime createdAt;
}
