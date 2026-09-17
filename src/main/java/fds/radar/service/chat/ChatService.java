package fds.radar.service.chat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import fds.radar.common.ChatSenderType;
import fds.radar.common.ChatSessionStatus;
import fds.radar.dto.chat.AdminChatResponse;
import fds.radar.dto.chat.ChatActiveSessionResponseDTO;
import fds.radar.dto.chat.ChatMessageDTO;
import fds.radar.dto.chat.ChatSessionListDTO;
import fds.radar.dto.chat.ChatSessionResponseDTO;
import fds.radar.entity.chat.ChatMessages;
import fds.radar.entity.chat.ChatSessions;
import fds.radar.entity.user.Users;
import fds.radar.exception.BusinessException;
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
    private final SimpMessagingTemplate messagingTemplate;

    // 사용자별 상담원 세션 생성 동시성 제어용 락 - userId당 객체 하나씩 재사용
    // (단일 인스턴스 환경 가정. 여러 서버로 분산 배포 시엔 DB 레벨 락으로 교체 필요)
    private final ConcurrentHashMap<Object, Object> adminSessionLocks = new ConcurrentHashMap<>();

    // 트랜잭션이 실제로 커밋된 후에만 소켓 알림 발송 - 커밋 전에 보내면 수신 측이 아직 반영 안 된 DB를 조회하는 레이스가 생김
    private void notifyAdminChats(Long sessionId) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override 
                public void afterCommit() {
                    messagingTemplate.convertAndSend("/topic/admin/chats", sessionId);
                }
            });
        } else {
            messagingTemplate.convertAndSend("/topic/admin/chats", sessionId);
        }
    }

    // 활성 세션 조회 or 생성 - 사용자당 활성 세션은 항상 1개
    @Transactional
    public ChatSessionResponseDTO getOrCreateSession(Long userId, String guestId) {
        validateIdentity(userId, guestId);

        ChatSessions session = (userId != null)
            ? chatSessionsRepository.findByUser_UserIdAndStatus(userId, ChatSessionStatus.OPEN)
                                    .orElseGet(() -> createSession(userId, null))
            : chatSessionsRepository.findByGuestIdentifierAndStatus(guestId, ChatSessionStatus.OPEN)
                                    .orElseGet(() -> createSession(null, guestId));

        return toResponseDTOWithMessages(session);
    }

    // 상담원 세션 조회 or 생성 - 봇 세션(OPEN)과 별개 트릭. "상담원 연결" 액션(배너/고객센터)에서만 호출
    @Transactional 
    public ChatSessionResponseDTO getOrCreateAdminSession(Long userId, String guestId) {
        validateIdentity(userId, guestId);

        Object lockKey = (userId != null) ? userId : guestId;
        Object lock = adminSessionLocks.computeIfAbsent(lockKey, k -> new Object());
        synchronized (lock) {
            ChatSessions session = (userId != null)
                ? chatSessionsRepository.findByUser_UserIdAndStatusIn(userId, List.of(ChatSessionStatus.WAITING, ChatSessionStatus.IN_PROGRESS))
                                        .orElseGet(() -> createAdminSession(userId, null))
                : chatSessionsRepository.findByGuestIdentifierAndStatusIn(guestId, List.of(ChatSessionStatus.WAITING, ChatSessionStatus.IN_PROGRESS))
                                        .orElseGet(() -> createAdminSession(null, guestId));

            return toResponseDTOWithMessages(session);
        }
    }

    private ChatSessions createAdminSession(Long userId, String guestId) {
        ChatSessions.ChatSessionsBuilder builder = ChatSessions.builder()
                                                               .status(ChatSessionStatus.WAITING)
                                                               .createdAt(LocalDateTime.now());

        if (userId != null) {
            Users user = userRepository.findById(userId)
                                       .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
            builder.user(user);
        } else {
            builder.guestIdentifier(guestId);
        }

        return chatSessionsRepository.save(builder.build());
    }

    private ChatSessions createSession(Long userId, String guestId) {
        ChatSessions.ChatSessionsBuilder builder = ChatSessions.builder()
                                                               .createdAt(LocalDateTime.now());

        if (userId != null) {
            Users user = userRepository.findById(userId)
                                       .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
            builder.user(user);
        } else {
            builder.guestIdentifier(guestId);
        }

        return chatSessionsRepository.save(builder.build());
    }

    // 위젯을 열 때 배너 표시용 - 상담원 관련 진행 중(WAITING/IN_PROGRESS) 세션만 가볍게 확인
    @Transactional(readOnly=true)
    public ChatActiveSessionResponseDTO getActiveAdminSession(Long userId, String guestId) {
        validateIdentity(userId, guestId);

        var sessionOpt = (userId != null)
            ? chatSessionsRepository.findByUser_UserIdAndStatusIn(userId, List.of(ChatSessionStatus.WAITING, ChatSessionStatus.IN_PROGRESS))
            : chatSessionsRepository.findByGuestIdentifierAndStatusIn(guestId, List.of(ChatSessionStatus.WAITING, ChatSessionStatus.IN_PROGRESS));

        return sessionOpt.map(session -> ChatActiveSessionResponseDTO.builder()
                                            .hasActiveSession(true)
                                            .sessionId(session.getSessionId())
                                            .status(session.getStatus())
                                            .build())
                         .orElse(ChatActiveSessionResponseDTO.builder().hasActiveSession(false).build());

    }

    // userId/guestId 둘 다 없거나 둘 다 있으면 안됨 - 정확히 하나만 허용
    private void validateIdentity(Long userId, String guestId) {
        boolean hasUser = userId != null;
        boolean hasGuest = guestId != null && !guestId.isBlank();

        if (hasUser == hasGuest) {
            throw new BusinessException("CHAT_IDENTITY_REQUIRED", 400, "사용자 식별 정보가 필요합니다.");
        }
    }

    // sessionId 기반 API 공통 접근 검증 - 세션 소유자(로그인 유저 or 게스트) 또는 ADMIN만 허용
    private void verifyAccess(ChatSessions session, Long userId, String guestId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = auth != null && auth.getAuthorities().stream()
                                              .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            return;
        }

        if (session.getUser() != null) {
            if (userId == null || !session.getUser().getUserId().equals(userId)) {
                throw new BusinessException("CHAT_ACCESS_DENIED", 403, "접근 권한이 없습니다.");
            }
        } else {
            if (guestId == null || !guestId.equals(session.getGuestIdentifier())) {
                throw new BusinessException("CHAT_ACCESS_DENIED", 403, "접근 권한이 없습니다.");
            }
        }
    }

    // 세션 ID로 직접 조회(메시지 이력 포함) - 관리자는 전체, 아니면 본인 세션만
    @Transactional(readOnly=true)
    public ChatSessionResponseDTO getSessionById(Long sessionId, Long userId, String guestId) {
        ChatSessions session = chatSessionsRepository.findById(sessionId)
                                                     .orElseThrow(() -> new IllegalArgumentException("세션을 찾을 수 없습니다."));

        verifyAccess(session, userId, guestId);

        return toResponseDTOWithMessages(session);
    }

    // 새 대화 시작 - 현재 세션 CLOSED 처리(이력은 DB에 저장)
    @Transactional
    public void closeSession(Long sessionId, Long userId, String guestId) {
        ChatSessions session = chatSessionsRepository.findById(sessionId)
                                                     .orElseThrow(() -> new IllegalArgumentException("세션을 찾을 수 없습니다."));

        verifyAccess(session, userId, guestId);

        session.setStatus(ChatSessionStatus.CLOSED);
        session.setClosedAt(LocalDateTime.now());
    }

    // 메시지 저장(BOT/USER/ADMIN 공용)
    @Transactional 
    public ChatMessageDTO saveMessage(Long sessionId, Long userId, String guestId, ChatSenderType senderType, Long senderId, String content) {
        return saveMessage(sessionId, userId, guestId, senderType, senderId, content, true);
    }

    // triggersInProgress=false로 넘기면 ADMIN 메시지여도 WAITING->IN_PROGRESS 전환을 일으키지 않음
    // (자동배정 시 시스템이 대신 보내는 인사말 전용 - 관리자의 실제 응답 아님)
    @Transactional 
    public ChatMessageDTO saveMessage(Long sessionId, Long userId, String guestId, ChatSenderType senderType, Long senderId, String content, boolean triggersInProgress) {
        
        ChatSessions session = chatSessionsRepository.findById(sessionId)
                                                     .orElseThrow(() -> new IllegalArgumentException("세션을 찾을 수 없습니다."));

        verifyAccess(session, userId, guestId);

        return saveMessageRaw(session, senderType, senderId, content, triggersInProgress);
    }

    private ChatMessageDTO saveMessageRaw(ChatSessions session, ChatSenderType senderType, Long senderId, String content, boolean triggersInProgress) {
        Long sessionId = session.getSessionId();

        ChatMessages message = ChatMessages.builder()
                                           .session(session)
                                           .senderType(senderType)
                                           .senderId(senderId)
                                           .content(content)
                                           .createdAt(LocalDateTime.now())
                                           .build();

        ChatMessages saved = chatMessagesRepository.save(message);

        if (senderType == ChatSenderType.USER) {
            chatSessionsRepository.updateAdminUnread(sessionId, true);
            chatSessionsRepository.updateUserUnread(sessionId, false);
            notifyAdminChats(sessionId);
        } else if (senderType == ChatSenderType.ADMIN) {
            chatSessionsRepository.updateAdminUnread(sessionId, false);
            chatSessionsRepository.updateUserUnread(sessionId, true);
            if (triggersInProgress) {
                chatSessionsRepository.markInProgressIfWaitingStatus(sessionId);
            }
        }

        return toMessageDTO(saved);
    }

    // 세션의 pendingContext 갱신(고정 문구 전송 시 세팅, 후속 답변 처리 후 해제)
    @Transactional
    public void updatePendingContext(Long sessionId, Long userId, String guestId, String pendingContext) {
        ChatSessions session = chatSessionsRepository.findById(sessionId)
                                                     .orElseThrow(() -> new IllegalArgumentException("세션을 찾을 수 없습니다."));

        verifyAccess(session, userId, guestId);

        session.setPendingContext(pendingContext);
    }

    // 사용자가 상담원 연결 요청 - 가장 한가한 관리자로 자동 배정 + 인사말 발송
    @Transactional 
    public void requestAdmin(Long sessionId, Long userId, String guestId) {
        ChatSessions session = chatSessionsRepository.findById(sessionId)
                                                     .orElseThrow(() -> new IllegalArgumentException("세션을 찾을 수 없습니다."));

        verifyAccess(session, userId, guestId);

        Users admin = userRepository.findLeastBusyAdmin().orElse(null);
        if (admin == null) {
            chatSessionsRepository.markWaitingIfOpen(sessionId); // 배정 가능한 관리자가 없으면 OPEN -> WAITING 전환
            notifyAdminChats(sessionId);
            return;
        }

        // OPEN -> WAITING 전환 + 배정만 함(IN_PROGRESS 전환은 관리자가 실제 메시지를 보낼 때)
        chatSessionsRepository.markWaitingIfOpen(sessionId);
        int updated = chatSessionsRepository.assignAdminIfUnassigned(sessionId, admin.getUserId());
        if (updated == 0) {
            return; // 이미 배정된 세션(중복 요청) - 무시
        }

        notifyAdminChats(sessionId);

        ChatMessageDTO systemMessage = saveMessageRaw(session, ChatSenderType.SYSTEM, admin.getUserId(), "상담원(" + admin.getName() + ")이(가) 배정되었습니다.", true);
        messagingTemplate.convertAndSend("/topic/chat/" + sessionId, systemMessage);

        ChatMessageDTO greeting = saveMessageRaw(session, ChatSenderType.ADMIN, admin.getUserId(), "안녕하세요. 상담원 " + admin.getName() + "입니다😊\n무엇을 도와드릴까요?", false);
        messagingTemplate.convertAndSend("/topic/chat/" + sessionId, greeting);
    }

    // 관리자가 세션 열람(클레임) - 배정만 하고 상태는 유지(IN_PROGRESS 전환은 실제 첫 메시지 전송 시)
    // 이미 다른 관리자에게 배정된 세션이면 접근 차단
    @Transactional
    public void markInProgress(Long sessionId, Long adminId) {
        ChatSessions session = chatSessionsRepository.findById(sessionId)
                                                     .orElseThrow(() -> new IllegalArgumentException("세션을 찾을 수 없습니다."));

        Users assignedAdmin = session.getAssignedAdmin();
        if (assignedAdmin != null && !assignedAdmin.getUserId().equals(adminId)) {
            throw new BusinessException("CHAT_ACCESS_DENIED", 403, "접근 권한이 없습니다.");
        }

        if (session.getStatus() == ChatSessionStatus.CLOSED) {
            return; // 종료된 상담은 이력 열람만
        }

        chatSessionsRepository.updateAdminUnread(sessionId, false);

        if (assignedAdmin != null) {
            return; // 이미 내가 배정받은 세션 - 재열람일 뿐, 추가 처리 없음
        }        

        int updated = chatSessionsRepository.assignAdminIfUnassigned(sessionId, adminId);
        if (updated == 0) {
            return; // 동시 요청으로 다른 관리자가 먼저 배정받은 극히 드문 race - 조용히 무시
        }

        Users admin = userRepository.findById(adminId)
                                    .orElseThrow(() -> new IllegalArgumentException("관리자를 찾을 수 없습니다."));

        ChatMessageDTO systemMessage = saveMessageRaw(session, ChatSenderType.SYSTEM, adminId, "상담원(" + admin.getName() + ")이(가) 배정되었습니다.", true);
        messagingTemplate.convertAndSend("/topic/chat/" + sessionId, systemMessage);
    }

    // 사용자가 챗봇 위젯을 열람 - 관리자 답장 읽음 처리
    @Transactional 
    public void markUserRead(Long sessionId, Long userId, String guestId) {
        ChatSessions session = chatSessionsRepository.findById(sessionId)
                                                     .orElseThrow(() -> new IllegalArgumentException("세션을 찾을 수 없습니다."));

        verifyAccess(session, userId, guestId);
        chatSessionsRepository.updateUserUnread(sessionId, false);
    }

    // 관리자용 - 미완료 세션 목록 (WAITING + IN_PROGRESS)
    @Transactional(readOnly=true)
    public List<ChatSessionListDTO> getSessions(List<ChatSessionStatus> statuses, Long adminId) {
        List<ChatSessions> sessions = (adminId != null)
            ? chatSessionsRepository.findByAssignedAdmin_UserIdAndStatusInOrderByCreatedAtAsc(adminId, statuses)
            : chatSessionsRepository.findByStatusInOrderByCreatedAtAsc(statuses);
        return sessions.stream()
                       .map(this::toListDTO)
                       .toList();
    }

    // 사용자 본인의 상담 내역 - 봇 전용(OPEN) 세션은 제외, 상담원 관련 세션(WAITING/IN_PROGRESS/CLOSED)만 조회
    @Transactional(readOnly=true)
    public List<ChatSessionListDTO> getSessionHistory(Long userId) {
        return chatSessionsRepository.findByUser_UserIdOrderByCreatedAtDesc(userId)
                                     .stream()
                                     .filter(session -> session.getStatus() != ChatSessionStatus.OPEN)
                                     .map(this::toListDTO)
                                     .toList();
    }

    // 관리자 대시보드용 - 상담 현황 요약(FraudCaseAdminController에서 병합)
    @Transactional(readOnly=true)
    public AdminChatResponse getDashboardStats(Long adminId) {
        long totalWaitingChatCount = chatSessionsRepository.countByStatus(ChatSessionStatus.WAITING);
        long myWaitingChatCount = chatSessionsRepository.countByAssignedAdmin_UserIdAndStatus(adminId, ChatSessionStatus.WAITING);
        long myInProgressChatCount = chatSessionsRepository.countByAssignedAdmin_UserIdAndStatus(adminId, ChatSessionStatus.IN_PROGRESS);

        return AdminChatResponse.builder()
                                    .totalWaitingChatCount(totalWaitingChatCount)
                                    .myWaitingChatCount(myWaitingChatCount)
                                    .myInProgressChatCount(myInProgressChatCount)
                                    .build();
    }

    private ChatSessionResponseDTO toResponseDTOWithMessages(ChatSessions session) {
        List<ChatMessageDTO> messages = chatMessagesRepository.findBySession_SessionIdOrderByCreatedAtAsc(session.getSessionId())
                                                              .stream()
                                                              .map(this::toMessageDTO)
                                                              .toList();

        return ChatSessionResponseDTO.builder()
                                     .sessionId(session.getSessionId())
                                     .userId(session.getUser() != null ? session.getUser().getUserId() : null)
                                     .userName(session.getUser() != null ? session.getUser().getName() : "게스트")
                                     .status(session.getStatus())
                                     .pendingContext(session.getPendingContext())
                                     .createdAt(session.getCreatedAt())
                                     .closedAt(session.getClosedAt())
                                     .adminUnread(session.isAdminUnread())
                                     .userUnread(session.isUserUnread())
                                     .messages(messages)
                                     .build();
    }

    private ChatSessionListDTO toListDTO(ChatSessions session) {
        String preview = chatMessagesRepository.findTopBySession_SessionIdOrderByCreatedAtDesc(session.getSessionId())
                                               .map(ChatMessages::getContent)
                                               .orElse("");

        return ChatSessionListDTO.builder()
                                 .sessionId(session.getSessionId())
                                 .userId(session.getUser() != null ? session.getUser().getUserId() : null)
                                 .userName(session.getUser() != null ? session.getUser().getName() : "게스트")
                                 .status(session.getStatus())
                                 .createdAt(session.getCreatedAt())
                                 .closedAt(session.getClosedAt())
                                 .lastMessagePreview(preview)
                                 .adminUnread(session.isAdminUnread())
                                 .assignedAdminName(session.getAssignedAdmin() != null ? session.getAssignedAdmin().getName() : null)
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
