package fds.radar.controller.user;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fds.radar.dto.user.LoginHistoriesResponse;
import fds.radar.service.user.LoginHistoryService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users/{userId}/login-histories")
@RequiredArgsConstructor
public class LoginHistoryController {

    private final LoginHistoryService loginHistoryService;

    @GetMapping
    public ResponseEntity<List<LoginHistoriesResponse>> getHistories(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                loginHistoryService.getHistories(userId)
        );
    }
}