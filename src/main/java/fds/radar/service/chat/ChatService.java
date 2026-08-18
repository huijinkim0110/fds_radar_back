package fds.radar.service.chat;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fds.radar.common.ChatSenderType;
import fds.radar.common.ChatSessionStatus;
import fds.radar.dto.chat.ChatMessageDTO;
import fds.radar.dto.chat.ChatSessionListDTO;
import fds.radar.dto.chat.ChatSessionResponseDTO;
import fds.radar.entity.chat.ChatMessages;
import fds.radar.entity.chat.ChatSessions;
import fds.radar.entity.user.Users;
import fds.radar.repository.chat.ChatMessagesRepository;
import fds.radar.repository.chat.ChatSessionsRepository;
import fds.radar.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatService {
    
    private final ChatSessionsRepository chatSessionsRepository;
    private final ChatMessagesRepository chatMessagesRepository;
    private final UserRepository userRepository;

    // 활성 세션 조회 or 생성 - 사용자당 활성 세션은 항상 1개
    @Transactional
    public ChatSessionResponseDTO getOrCreateSession(Long userId) {
        ChatSessions session = chatSessionsRepository.findByUser_UserIdAndStatusNot(userId, ChatSessionStatus.CLOSED)
                                                     .orElseGet(() -> createSession(userId));

        return toResponseDTOWithMessages(session);
    }

    private ChatSessions createSession(Long userId) {
        Users user = userRepository.findById(userId)
                                   .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        ChatSessions session = ChatSessions.builder()
                                           .user(user)
                                           .createdAt(LocalDateTime.now())
                                           .build();
        
        return chatSessionsRepository.save(session);
    }

    // 새 대화 시작 - 현재 세션 CLOSED 처리(이력은 DB에 저장)
    @Transactional
    public void closeSession(Long sessionId) {
        ChatSessions session = chatSessionsRepository.findById(sessionId)
                                                     .orElseThrow(() -> new IllegalArgumentException("세션을 찾을 수 없습니다."));

        session.setStatus(ChatSessionStatus.CLOSED);
        session.setClosedAt(LocalDateTime.now());
    }

    // 메시지 저장(BOT/USER/ADMIN 공용)
    public ChatMessageDTO saveMessage(Long sessionId, ChatSenderType senderType, Long senderId, String content) {
        ChatSessions session = chatSessionsRepository.findById(sessionId)
                                                     .orElseThrow(() -> new IllegalArgumentException("세션을 찾을 수 없습니다."));

        ChatMessages message = ChatMessages.builder()
                                           .session(session)
                                           .senderType(senderType)
                                           .senderId(senderId)
                                           .content(content)
                                           .createdAt(LocalDateTime.now())
                                           .build();

        ChatMessages saved = chatMessagesRepository.save(message);
        return toMessageDTO(saved);
    }

    // 관리자가 세션 열람 - WAITING -> IN_PROGRESS 전환
    @Transactional
    public void markInProgress(Long sessionId, Long adminId) {
        ChatSessions session = chatSessionsRepository.findById(sessionId)
                                                     .orElseThrow(() -> new IllegalArgumentException("세션을 찾을 수 없습니다."));

        if (session.getStatus() == ChatSessionStatus.WAITING) {
            Users admin = userRepository.findById(adminId)
                                        .orElseThrow(() -> new IllegalArgumentException("관리자를 찾을 수 없습니다."));

            session.setStatus(ChatSessionStatus.IN_PROGRESS);
            session.setAssignedAdmin(admin);
        }
    }

    // 관리자용 - 미완료 세션 목록 (WAITING + IN_PROGRESS)
    @Transactional(readOnly=true)
    public List<ChatSessionListDTO> getActiveSessions() {
        List<ChatSessions> sessions = chatSessionsRepository.findByStatusInOrderByCreatedAtAsc(
            List.of(ChatSessionStatus.WAITING, ChatSessionStatus.IN_PROGRESS));

        return sessions.stream()
                       .map(this::toListDTO)
                       .toList();
    }

    private ChatSessionResponseDTO toResponseDTOWithMessages(ChatSessions session) {
        List<ChatMessageDTO> messages = chatMessagesRepository.findBySession_SessionIdOrderByCreatedAtAsc(session.getSessionId())
                                                              .stream()
                                                              .map(this::toMessageDTO)
                                                              .toList();

        return ChatSessionResponseDTO.builder()
                                     .sessionId(session.getSessionId())
                                     .userId(session.getUser().getUserId())
                                     .status(session.getStatus())
                                     .createdAt(session.getCreatedAt())
                                     .closedAt(session.getClosedAt())
                                     .messages(messages)
                                     .build();
    }

    private ChatSessionListDTO toListDTO(ChatSessions session) {
        String preview = chatMessagesRepository.findTopBySession_SessionIdOrderByCreatedAtDesc(session.getSessionId())
                                               .map(ChatMessages::getContent)
                                               .orElse("");

        return ChatSessionListDTO.builder()
                                 .sessionId(session.getSessionId())
                                 .userId(session.getUser().getUserId())
                                 .userName(session.getUser().getName())
                                 .status(session.getStatus())
                                 .createdAt(session.getCreatedAt())
                                 .lastMessagePreview(preview)
                                 .build();
    }

    private ChatMessageDTO toMessageDTO(ChatMessages message) {
        return ChatMessageDTO.builder()
                             .messageId(message.getMessageId())
                             .sessionId(message.getSession().getSessionId())
                             .senderType(message.getSenderType())
                             .senderId(message.getSenderId())
                             .content(message.getContent())
                             .createdAt(message.getCreatedAt())
                             .build();
    }
}
