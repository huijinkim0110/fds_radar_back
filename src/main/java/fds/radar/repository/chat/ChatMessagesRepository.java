package fds.radar.repository.chat;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import fds.radar.entity.chat.ChatMessages;

public interface ChatMessagesRepository extends JpaRepository<ChatMessages, Long> {
    // 특정 세션의 메시지 이력 조회(시간순)
    List<ChatMessages> findBySession_SessionIdOrderByCreatedAtAsc(Long sessionId);

    // 특정 세션의 마지막 메시지 하나만 조회(목록 미리보기용)
    Optional<ChatMessages> findTopBySession_SessionIdOrderByCreatedAtDesc(Long sessionId);
}
