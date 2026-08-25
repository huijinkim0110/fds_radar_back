package fds.radar.dto.chat;

import java.time.LocalDateTime;
import java.util.List;

import fds.radar.common.ChatSessionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatSessionResponseDTO {
    private Long sessionId;
    private Long userId;
    private ChatSessionStatus status;
    private String pendingContext;
    private LocalDateTime createdAt;
    private LocalDateTime closedAt;
    private List<ChatMessageDTO> messages; // 세션 상세 조회시에만 채움, 목록 조회 시엔 null
}
