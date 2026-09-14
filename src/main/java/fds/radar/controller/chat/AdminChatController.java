package fds.radar.controller.chat;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fds.radar.common.ChatSessionStatus;
import fds.radar.dto.chat.ChatSessionListDTO;
import fds.radar.service.chat.ChatService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/chats")
@RequiredArgsConstructor
public class AdminChatController {
    
    private final ChatService chatService;

    // 상담 목록 - status 선택 안하면 전체(WAITING + IN_PROGRESS + CLOSED), adminId 있으면 "내 상담"만
    // GET /admin/chats?status=WAITING&status=CLOSED&adminId=1
    @GetMapping
    public ResponseEntity<List<ChatSessionListDTO>> getSessions(
            @RequestParam(required=false) List<ChatSessionStatus> status,
            @RequestParam(required=false) Long adminId) {

        List<ChatSessionStatus> statuses = (status == null || status.isEmpty())
            ? List.of(ChatSessionStatus.WAITING, ChatSessionStatus.IN_PROGRESS, ChatSessionStatus.CLOSED)
            : status;

        return ResponseEntity.ok(chatService.getSessions(statuses, adminId));
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
