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

import fds.radar.dto.user.DeviceRegisterRequest;
import fds.radar.dto.user.DeviceResponse;
import fds.radar.service.user.UserDeviceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users/{userId}/devices")
@RequiredArgsConstructor
public class UserDeviceController {

    private final UserDeviceService userDeviceService;

    @PostMapping
    public ResponseEntity<DeviceResponse> registerOrUpdateDevice(
            @PathVariable Long userId,
            @Valid @RequestBody DeviceRegisterRequest request) {

        DeviceResponse response =
                userDeviceService.registerOrUpdateDevice(userId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<DeviceResponse>> getDevices(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                userDeviceService.getDevices(userId)
        );
    }

    @PatchMapping("/{deviceId}/trust")
    public ResponseEntity<Void> trustDevice(
            @PathVariable Long userId,
            @PathVariable Long deviceId) {

        userDeviceService.trustDevice(deviceId);

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{deviceId}/block")
    public ResponseEntity<Void> blockDevice(
            @PathVariable Long userId,
            @PathVariable Long deviceId) {

        userDeviceService.blockDevice(deviceId);

        return ResponseEntity.ok().build();
    }
}