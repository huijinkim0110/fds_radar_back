package fds.radar.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatPendingContextUpdateRequestDTO {
    private String pendingContext; // null이면 해제
}
