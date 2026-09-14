package fds.radar.repository.chat;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import fds.radar.common.ChatSessionStatus;
import fds.radar.entity.chat.ChatSessions;

public interface ChatSessionsRepository extends JpaRepository<ChatSessions, Long> {
    // 사용자의 활성 세션(CLOSED 아닌 것) 조회 - 재입장 시 이어서 쓸 세션 찾기용
    Optional<ChatSessions> findByUser_UserIdAndStatusNot(Long userId, ChatSessionStatus excludedStatus);

    // 사용자의 봇 대화(OPEN) 세션 조회 - 상담원 세션과 별개로 관리
    Optional<ChatSessions> findByUser_UserIdAndStatus(Long userId, ChatSessionStatus status);

    // 사용자의 상담원 관련 진행 중 세션 조회(WAITING/IN_PROGRESS) - 위젯 열 때 배너 표시용
    Optional<ChatSessions> findByUser_UserIdAndStatusIn(Long userId, List<ChatSessionStatus> statuses);

    // 관리자용 - WAITING(미배정) + IN_PROGRESS(본인이 배정된 것) 상태 세션 목록
    List<ChatSessions> findByStatusInOrderByCreatedAtAsc(List<ChatSessionStatus> statuses);

    // 관리자용 - "내 상담" 필터: 특정 관리자에게 배정된 세션 중 선택한 상태들만 조회
    List<ChatSessions> findByAssignedAdmin_UserIdAndStatusInOrderByCreatedAtAsc(Long adminId, List<ChatSessionStatus> statuses);

    // 사용자의 전체 세션 이력
    List<ChatSessions> findByUser_UserIdOrderByCreatedAtDesc(Long userId);

    // 대시보드용 - 상태별 전체 건수 (관리자 무관, 시스템 전체 대기건수 등)
    long countByStatus(ChatSessionStatus status);

    // 대시보드용 - 특정 관리자에게 배정된 상태별 건수(내 대기/내 진행중)
    long countByAssignedAdmin_UserIdAndStatus(Long adminId, ChatSessionStatus status);

    // 관리자 배정만 원자적으로 수행(상태는 변경하지 않음) - 자동 배정, 수동 열람(클레임) 공용
    // 이미 배정된 세션이면 무시(0 반환)해서 중복 배정 방지
    @Modifying 
    @Query(value="UPDATE chat_sessions SET assigned_admin_id = :adminId " + 
                 "WHERE session_id = :sessionId AND assigned_admin_id IS NULL", nativeQuery = true)
    int assignAdminIfUnassigned(@Param("sessionId") Long sessionId, @Param("adminId") Long adminId);

    // WAITING인 세션에 한해 IN_PROGRESS로 전환 - 관리자가 실제 첫 메시지를 보낼 때 호출
    @Modifying 
    @Query(value="UPDATE chat_sessions SET status = 'IN_PROGRESS' " +
                 "WHERE session_id = :sessionId AND status = 'WAITING'", nativeQuery = true)
    int markInProgressIfWaitingStatus(@Param("sessionId") Long sessionId);

    // 배정 가능한 관리자가 없을 때 OPEN -> WAITING 전환(이미 WAITING/IN_PROGRESS/CLOSED이면 무시)
    @Modifying 
    @Query(value="UPDATE chat_sessions SET status = 'WAITING' WHERE session_id = :sessionId AND status = 'OPEN'", nativeQuery = true)
    int markWaitingIfOpen(@Param("sessionId") Long sessionId);

    @Modifying 
    @Query(value="UPDATE chat_sessions SET admin_unread = :adminUnread WHERE session_id = :sessionId", nativeQuery = true)
    void updateAdminUnread(@Param("sessionId") Long sessionId, @Param("adminUnread") boolean adminUnread);

    @Modifying 
    @Query(value="UPDATE chat_sessions SET user_unread = :userUnread WHERE session_id = :sessionId", nativeQuery = true)
    void updateUserUnread(@Param("sessionId") Long sessionId, @Param("userUnread") boolean userUnread);
}
