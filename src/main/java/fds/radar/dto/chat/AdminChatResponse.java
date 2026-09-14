package fds.radar.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// 관리자 마이페이지 대시보드 응답(chat 도메인 부분): 상담 현황 요약
@Data 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder 
public class AdminChatResponse {
    private long totalWaitingChatCount; // 시스템 전체 WAITING 건수(배정자 무관)
    private long myWaitingChatCount; // 나에게 배정됐지만 아직 응답 전인 건수
    private long myInProgressChatCount; // 나에게 배정되고 내가 응답한(진행중) 건수
}
