package fds.radar.entity.chat;

import java.time.LocalDateTime;

import fds.radar.common.ChatSessionStatus;
import fds.radar.entity.user.Users;
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
public class ChatSessions {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long sessionId;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=false)
    private Users user;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ChatSessionStatus status = ChatSessionStatus.WAITING;

    // 직전에 보낸 고정 안내 문구의 키(NO_PRODUCTS / DIAGNOSIS_REQUIRED / NO_GOALS 등)
    // null이면 "대기 중인 후속 질문 없음" -> 일반 분류 흐름으로 처리
    private String pendingContext;

    // TODO: A 인증 만들어서 TEMP_ADMIN_ID 대체
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="assigned_admin_id", nullable=true)
    private Users assignedAdmin;

    private LocalDateTime createdAt;
    private LocalDateTime closedAt;
}
