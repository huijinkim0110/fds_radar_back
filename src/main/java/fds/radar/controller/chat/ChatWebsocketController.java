package fds.radar.controller.chat;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import fds.radar.dto.chat.ChatMessageDTO;
import fds.radar.dto.chat.ChatSendMessageRequestDTO;
import fds.radar.service.chat.ChatService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChatWebsocketController {
    
    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    // 클라이언트가 /app/chat/{sessionId}로 전송 -> 저장 후 /topic/chat/{sessionId}로 브로드캐스트
    @MessageMapping("/chat/{sessionId}")
    public void sendMessage(@DestinationVariable Long sessionId, ChatSendMessageRequestDTO request) {
        ChatMessageDTO saved = chatService.saveMessage(sessionId, request.getSenderType(), request.getSenderId(), request.getContent());
        messagingTemplate.convertAndSend("/topic/chat/" + sessionId, saved);
    }
}
