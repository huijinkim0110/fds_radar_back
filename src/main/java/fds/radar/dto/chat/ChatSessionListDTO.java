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
    // 관리자용 목록
    private Long sessionId;
    private Long userId;
    private String userName; // 목록에서 누구 문의인지 바로 보이게
    private ChatSessionStatus status;
    private LocalDateTime createdAt;
    private String lastMessagePreview; // 목록에서 마지막 메시지 미리보기
}
