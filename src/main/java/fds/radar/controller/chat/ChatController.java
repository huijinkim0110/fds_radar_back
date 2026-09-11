package fds.radar.controller.chat;

import fds.radar.service.chat.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fds.radar.dto.chat.ChatActiveSessionResponseDTO;
import fds.radar.dto.chat.ChatMessageDTO;
import fds.radar.dto.chat.ChatPendingContextUpdateRequestDTO;
import fds.radar.dto.chat.ChatSendMessageRequestDTO;
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

    // 상담원 세션 조회 or 생성 - "상담원 연결" 액션(배너/고객센터)에서 호출. 봇 세션과 별개 트릭
    // GET /chat/sessions/admin?userId=1
    @GetMapping("/admin")
    public ResponseEntity<ChatSessionResponseDTO> getOrCreateAdminSession(@RequestParam Long userId) {
        return ResponseEntity.ok(chatService.getOrCreateSession(userId));
    }

    // 챗봇 위젯을 열 때 배너 표시용 - 상담원 관련 진행 중 세션만 가볍게 확인(메시지 이력 없음)
    // GET /chat/sessions/active-admin?userId=1
    @GetMapping("/active-admin")
    public ResponseEntity<ChatActiveSessionResponseDTO> getActiveAdminSession(@RequestParam Long userId) {
        return ResponseEntity.ok(chatService.getActiveAdminSession(userId));
    }
    
    // 자유입력 메시지 저장(USER/BOT 공용) - FastAPI 응답을 받은 후 프론트에서 호출
    // POST /chat/sessions/{sessionId}/messages
    @PostMapping("/{sessionId}/messages")
    public ResponseEntity<ChatMessageDTO> saveMessage(
            @PathVariable Long sessionId,
            @RequestBody ChatSendMessageRequestDTO request) {
        
        ChatMessageDTO saved = chatService.saveMessage(sessionId, request.getSenderType(), request.getSenderId(), request.getContent());
        return ResponseEntity.ok(saved);
    }

    // 세션 ID로 직접 조회(관리자용 - userId 필요없음)
    @GetMapping("/{sessionId}")
    public ResponseEntity<ChatSessionResponseDTO> getSessionById(@PathVariable Long sessionId) {
        return ResponseEntity.ok(chatService.getSessionById(sessionId));
    }

    // 사용자가 챗봇 위젯 열람 - 관리자 답장 읽음 처리
    // PATCH /chat/sessions/{sessionId}/user-read
    @PatchMapping("/{sessionId}/user-read")
    public ResponseEntity<Void> markUserUnread(@PathVariable Long sessionId) {
        chatService.markUserRead(sessionId);
        return ResponseEntity.noContent().build();
    }

    // 상담원 연결 요청 - 자동 배정
    // POST /chat/sessions/{sessionId}/request-admin
    @PostMapping("/{sessionId}/request-admin")
    public ResponseEntity<Void> requestAdmin(@PathVariable  Long sessionId) {
        chatService.requestAdmin(sessionId);
        return ResponseEntity.noContent().build();
    }

    // 새 대화 시작 - 현재 세션 닫기
    // POST /chat/sessions/{sessionId}/close
    @PostMapping("/{sessionId}/close")
    public ResponseEntity<Void> closeSession(@PathVariable Long sessionId) {
        chatService.closeSession(sessionId);
        return ResponseEntity.noContent().build();
    }

    // pendingContext 갱신(FastAPI가 고정 문구 전송/후속 답변 처리 시 호출)
    // PATCH /chat/sessions/{sessionId}/pending-context
    @PatchMapping("/{sessionId}/pending-context")
    public ResponseEntity<Void> updatePendingContext(
            @PathVariable Long sessionId,
            @RequestBody ChatPendingContextUpdateRequestDTO request) {

        chatService.updatePendingContext(sessionId, request.getPendingContext());
        return ResponseEntity.noContent().build();
    }
}
