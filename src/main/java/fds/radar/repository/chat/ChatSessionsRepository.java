package fds.radar.repository.chat;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import fds.radar.common.ChatSessionStatus;
import fds.radar.entity.chat.ChatSessions;

public interface ChatSessionsRepository extends JpaRepository<ChatSessions, Long> {
    // 사용자의 활성 세션(CLOSED 아닌 것) 조회 - 재입장 시 이어서 쓸 세션 찾기용
    Optional<ChatSessions> findByUser_UserIdAndStatusNot(Long userId, ChatSessionStatus excludedStatus);

    // 관리자용 - WAITING(미배정) + IN_PROGRESS(본인이 배정된 것) 상태 세션 목록
    List<ChatSessions> findByStatusInOrderByCreatedAtAsc(List<ChatSessionStatus> statuses);

    // 사용자의 전체 세션 이력
    List<ChatSessions> findByUser_UserIdOrderByCreatedAtDesc(Long userId);
}
