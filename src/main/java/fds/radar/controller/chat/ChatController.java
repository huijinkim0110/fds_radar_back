package fds.radar.controller.chat;

import fds.radar.service.chat.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fds.radar.dto.chat.ChatSessionResponseDTO;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/chat/sessions")
@RequiredArgsConstructor
public class ChatController {
    
    private final ChatService chatService;

    // 활성 세션 조회 or 생성
    // GET /chat/sessions?userId=1
    @GetMapping
    public ResponseEntity<ChatSessionResponseDTO> getOrCreateSession(@RequestParam Long userId) {
        return ResponseEntity.ok(chatService.getOrCreateSession(userId));
    }

    // 세션 ID로 직접 조회(관리자용 - userId 필요없음)
    @GetMapping("/{sessionId}")
    public ResponseEntity<ChatSessionResponseDTO> getSessionById(@PathVariable Long sessionId) {
        return ResponseEntity.ok(chatService.getSessionById(sessionId));
    }

    // 새 대화 시작 - 현재 세션 닫기
    // POST /chat/sessions/{sessionId}/close
    @PostMapping("/{sessionId}/close")
    public ResponseEntity<Void> closeSession(@PathVariable Long sessionId) {
        chatService.closeSession(sessionId);
        return ResponseEntity.noContent().build();
    }
}
