package fds.radar.dto.chat;

import java.time.LocalDateTime;

import fds.radar.common.ChatSessionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatSessionListDTO {
    // 관리자용 목록 + 사용자 본인 상담 내역 공용
    private Long sessionId;
    private Long userId;
    private String userName; // 목록에서 누구 문의인지 바로 보이게
    private ChatSessionStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime closedAt; // 상담 종료 시각 - 사용자 상담 내역에서 종료일 표시
    private String lastMessagePreview; // 목록에서 마지막 메시지 미리보기
    private boolean adminUnread;
    private String assignedAdminName; // 담당 관리자 이름 - 미배정(WAITING)이면 null
}
