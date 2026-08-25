package fds.radar.controller.user;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fds.radar.dto.user.NotificationRequest;
import fds.radar.dto.user.NotificationResponse;
import fds.radar.service.user.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users/{userId}/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    public ResponseEntity<NotificationResponse> createNotification(
            @PathVariable Long userId,
            @Valid @RequestBody NotificationRequest request) {

        NotificationResponse response =
                notificationService.createNotification(userId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getNotifications(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                notificationService.getNotifications(userId)
        );
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<Void> readNotification(
            @PathVariable Long userId,
            @PathVariable Long notificationId) {

        notificationService.readNotification(
                userId,
                notificationId
        );

        return ResponseEntity.ok().build();
    }
}