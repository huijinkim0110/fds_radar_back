package fds.radar.controller.admin;
/*
    * 관리자 마이페이지 대시보드 전용 컨트롤러
    * 사건 현황(FraudCaseService) + 상담 현황(ChatService)처럼 여러 도메인 데이터를
    * 한 화면에 모아 보여주는 응답을 조립하는 역할만 담당. 각 도메인 로직은 건드리지 않음
*/

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fds.radar.dto.admin.AdminDashboardResponse;
import fds.radar.dto.chat.AdminChatResponse;
import fds.radar.dto.fraud.AdminFraudResponse;
import fds.radar.service.chat.ChatService;
import fds.radar.service.fraud.FraudCaseService;
import lombok.RequiredArgsConstructor;

@RestController 
@RequestMapping ("/api/admin/dashboard")
@RequiredArgsConstructor 
public class AdminDashboardController {
    
    private final FraudCaseService fraudCaseService;
    private final ChatService chatService;

    // 관리자 마이페이지 대시보드 조회
    // TODO(로그인 기능 붙으면 수정): 지금은 로그인 기능이 없어서 adminId를 쿼리 파라미터로 임시로 받음.
    // 나중에 SecurityContextHolder에서 로그인한 관리자 id를 꺼내는 방식으로 교체
    @GetMapping ("/mypage")
    public ResponseEntity<AdminDashboardResponse> getDashboard(@RequestParam Long adminId) {
        AdminFraudResponse fraudDashboard = fraudCaseService.getDashboard(adminId);
        AdminChatResponse chatDashboard = chatService.getDashboardStats(adminId);

        AdminDashboardResponse merged = AdminDashboardResponse.builder()
                                                              .fraud(fraudDashboard)
                                                              .chat(chatDashboard)
                                                              .build();
        
        return ResponseEntity.ok(merged);
    }
}
