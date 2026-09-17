package fds.radar.controller.admin;
/*
    * 관리자 마이페이지 대시보드 전용 컨트롤러
    * 사건 현황(FraudCaseService) + 상담 현황(ChatService)처럼 여러 도메인 데이터를
    * 한 화면에 모아 보여주는 응답을 조립하는 역할만 담당. 각 도메인 로직은 건드리지 않음
*/

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fds.radar.dto.admin.AdminDashboardResponse;
import fds.radar.dto.chat.AdminChatResponse;
import fds.radar.dto.fraud.AdminFraudResponse;
import fds.radar.service.chat.ChatService;
import fds.radar.service.fraud.FraudCaseService;
import lombok.RequiredArgsConstructor;

@RestController 
@RequestMapping ("/api/admin/dashboard")
@PreAuthorize ("hasRole('ADMIN')")
@RequiredArgsConstructor 
public class AdminDashboardController {
    
    private final FraudCaseService fraudCaseService;
    private final ChatService chatService;

    // 관리자 마이페이지 대시보드 조회
    @GetMapping ("/mypage")
    public ResponseEntity<AdminDashboardResponse> getDashboard(@AuthenticationPrincipal Long adminId) {
        AdminFraudResponse fraudDashboard = fraudCaseService.getDashboard(adminId);
        AdminChatResponse chatDashboard = chatService.getDashboardStats(adminId);

        AdminDashboardResponse merged = AdminDashboardResponse.builder()
                                                              .fraud(fraudDashboard)
                                                              .chat(chatDashboard)
                                                              .build();
        
        return ResponseEntity.ok(merged);
    }
}
