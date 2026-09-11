package fds.radar.dto.admin;

import fds.radar.dto.chat.AdminChatResponse;
import fds.radar.dto.fraud.AdminFraudResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 관리자 마이페이지 대시보드 최종 응답: 기존 두 응답을 그대로 감싸기
@Getter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder 
public class AdminDashboardResponse {
    private AdminFraudResponse fraud;
    private AdminChatResponse chat;
}
