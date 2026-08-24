package fds.radar.controller.chat;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fds.radar.dto.chat.ChatSessionListDTO;
import fds.radar.service.chat.ChatService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/chats")
@RequiredArgsConstructor
public class AdminChatController {
    
    private final ChatService chatService;

    // 미완료 세션 목록
    // GET /admin/chats
    @GetMapping
    public ResponseEntity<List<ChatSessionListDTO>> getActiveSessions() {
        return ResponseEntity.ok(chatService.getActiveSessions());
    }

    // 세션 열람 - WAITING -> IN_PROGRESS 전환
    // PATCH /admin/chats/{sessionId}/read?adminId=1
    @PatchMapping("/{sessionId}/read")
    public ResponseEntity<Void> markInProgress(
            @PathVariable Long sessionId,
            @RequestParam Long adminId) {
        
        chatService.markInProgress(sessionId, adminId);
        return ResponseEntity.noContent().build();
    }
}
