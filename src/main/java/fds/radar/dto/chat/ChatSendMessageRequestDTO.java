package fds.radar.dto.chat;

import fds.radar.common.ChatSenderType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatSendMessageRequestDTO {
    private ChatSenderType senderType;
    private Long senderId; // BOT이면 null
    private String content;
}
