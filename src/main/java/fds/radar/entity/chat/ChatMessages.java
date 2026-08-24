package fds.radar.entity.chat;

import java.time.LocalDateTime;

import fds.radar.common.ChatSenderType;
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
public class ChatMessages {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long messageId;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="session_id", nullable=false)
    private ChatSessions session;

    @Enumerated(EnumType.STRING)
    private ChatSenderType senderType;

    // BOT일 때는 null, USER/ADMIN일 때만 값 채움
    private Long senderId;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createdAt;
}
