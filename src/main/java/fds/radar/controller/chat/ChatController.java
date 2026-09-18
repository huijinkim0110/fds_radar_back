package fds.radar.controller.chat;

import fds.radar.service.chat.ChatService;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import fds.radar.dto.chat.ChatSessionListDTO;
import fds.radar.dto.chat.ChatSessionResponseDTO;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/chat/sessions")
@RequiredArgsConstructor
public class ChatController {
    
    private final ChatService chatService;

    // 활성 세션 조회 or 생성
    @GetMapping
    public ResponseEntity<ChatSessionResponseDTO> getOrCreateSession(
            @AuthenticationPrincipal Long userId,
            @RequestParam (required=false) String guestId) {

        return ResponseEntity.ok(chatService.getOrCreateSession(userId, guestId));
    }

    // 상담원 세션 조회 or 생성 - "상담원 연결" 액션(배너/고객센터)에서 호출. 봇 세션과 별개 트릭
    @GetMapping("/admin")
    public ResponseEntity<ChatSessionResponseDTO> getOrCreateAdminSession(
            @AuthenticationPrincipal Long userId,
            @RequestParam (required=false) String guestId) {

        return ResponseEntity.ok(chatService.getOrCreateAdminSession(userId, guestId));
    }

    // 챗봇 위젯을 열 때 배너 표시용 - 상담원 관련 진행 중 세션만 가볍게 확인(메시지 이력 없음)
    @GetMapping("/active-admin")
    public ResponseEntity<ChatActiveSessionResponseDTO> getActiveAdminSession(
            @AuthenticationPrincipal Long userId,
            @RequestParam (required=false) String guestId) {

            return ResponseEntity.ok(chatService.getActiveAdminSession(userId, guestId));
    }
    
    // 자유입력 메시지 저장(USER/BOT 공용) - FastAPI 응답을 받은 후 프론트에서 호출
    @PostMapping("/{sessionId}/messages")
    public ResponseEntity<ChatMessageDTO> saveMessage(
            @AuthenticationPrincipal Long userId,
            @RequestParam (required=false) String guestId,
            @PathVariable Long sessionId,
            @RequestBody ChatSendMessageRequestDTO request) {
        
        ChatMessageDTO saved = chatService.saveMessage(sessionId, userId, guestId, request.getSenderType(), request.getSenderId(), request.getContent());
        return ResponseEntity.ok(saved);
    }

    // 사용자 본인의 상담 내역 조회(마이페이지 메뉴)
    // GET /chat/sessions/history?userId=1
    @GetMapping("/history")
    public ResponseEntity<List<ChatSessionListDTO>> getSessionHistory(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(chatService.getSessionHistory(userId));
    }

    // 세션 ID로 직접 조회(관리자용 - userId 필요없음)
    @GetMapping("/{sessionId}")
    public ResponseEntity<ChatSessionResponseDTO> getSessionById(
            @AuthenticationPrincipal Long userId,
            @RequestParam(required=false) String guestId,
            @PathVariable Long sessionId) {

        return ResponseEntity.ok(chatService.getSessionById(sessionId, userId, guestId));
    }

    // 사용자가 챗봇 위젯 열람 - 관리자 답장 읽음 처리
    @PatchMapping("/{sessionId}/user-read")
    public ResponseEntity<Void> markUserUnread(
            @AuthenticationPrincipal Long userId,
            @RequestParam (required=false) String guestId,
            @PathVariable Long sessionId) {
        
        chatService.markUserRead(sessionId, userId, guestId);
        return ResponseEntity.noContent().build();
    }

    // 상담원 연결 요청 - 자동 배정
    @PostMapping("/{sessionId}/request-admin")
    public ResponseEntity<Void> requestAdmin(
            @AuthenticationPrincipal Long userId,
            @RequestParam (required=false) String guestId,
            @PathVariable Long sessionId) {

        chatService.requestAdmin(sessionId, userId, guestId);
        return ResponseEntity.noContent().build();
    }

    // 새 대화 시작 - 현재 세션 닫기
    @PostMapping("/{sessionId}/close")
    public ResponseEntity<Void> closeSession(
            @AuthenticationPrincipal Long userId,
            @RequestParam (required=false) String guestId,
            @PathVariable Long sessionId) {

        chatService.closeSession(sessionId, userId, guestId);
        return ResponseEntity.noContent().build();
    }

    // pendingContext 갱신(FastAPI가 고정 문구 전송/후속 답변 처리 시 호출)
    @PatchMapping("/{sessionId}/pending-context")
    public ResponseEntity<Void> updatePendingContext(
            @AuthenticationPrincipal Long userId,
            @RequestParam (required=false) String guestId,
            @PathVariable Long sessionId,
            @RequestBody ChatPendingContextUpdateRequestDTO request) {

        chatService.updatePendingContext(sessionId, userId, guestId, request.getPendingContext());
        return ResponseEntity.noContent().build();
    }
}
