package fds.radar.dto.chat;

import java.time.LocalDateTime;

import fds.radar.common.ChatSenderType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageDTO {
    private Long messageId;
    private Long sessionId;
    private ChatSenderType senderType;
    private Long senderId; // BOT이면 null
    private String content;
    private LocalDateTime createdAt;
}
