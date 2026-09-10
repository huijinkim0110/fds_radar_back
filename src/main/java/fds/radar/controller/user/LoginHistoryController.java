package fds.radar.controller.user;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fds.radar.dto.user.LoginHistoriesRequest;
import fds.radar.dto.user.LoginHistoriesResponse;
import fds.radar.service.user.LoginHistoryService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users/{userId}/login-histories")
@RequiredArgsConstructor
public class LoginHistoryController {

    private final LoginHistoryService loginHistoryService;

    // 로그인 기록 저장
    @PostMapping
    public ResponseEntity<Void> recordLoginHistory(
            @PathVariable Long userId,
            @RequestBody LoginHistoriesRequest request,
            HttpServletRequest httpRequest) {

        String ipAddress = httpRequest.getRemoteAddr();

        loginHistoryService.record(
                userId,
                ipAddress,
                request
        );

        return ResponseEntity.ok().build();
    }

    // 로그인 기록 조회
    @GetMapping
    public ResponseEntity<List<LoginHistoriesResponse>> getHistories(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                loginHistoryService.getHistories(userId)
        );
    }
}