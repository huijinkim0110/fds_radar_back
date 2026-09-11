package fds.radar.dto.chat;

import fds.radar.common.ChatSessionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder 
public class ChatActiveSessionResponseDTO {
    // 챗봇 위젯을 열 때 배너 표시용 - WAITING/IN_PROGRESS 세션이 있는지만 가볍게 확인
    private boolean hasActiveSession;
    private Long sessionId;
    private ChatSessionStatus status; // hasActiveSession=false이면 null;
}
