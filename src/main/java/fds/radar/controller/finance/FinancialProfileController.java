package fds.radar.controller.finance;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fds.radar.config.OwnershipChecker;
import fds.radar.dto.financial.FinancialProfileRequest;
import fds.radar.dto.financial.FinancialProfileResponse;
import fds.radar.service.finance.FinancialProfileService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/financial-profiles")
@RequiredArgsConstructor
public class FinancialProfileController {

    private final FinancialProfileService financialProfileService;
    private final OwnershipChecker ownershipChecker;

    // 금융 프로필 존재 여부 확인
    @GetMapping("/exists")
    public ResponseEntity<Boolean> hasProfile(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(financialProfileService.hasProfile(userId));
    }

    // 본인 프로필 - 토큰만으로 조회 (숫자 userId 모르는 클라이언트용)
    @GetMapping("/me")
    public ResponseEntity<FinancialProfileResponse> getMyProfile(@AuthenticationPrincipal Long userId) {
        ownershipChecker.verify(userId); // 사실 본인이라 항상 통과하지만 일관성 위해 유지 가능, 또는 생략
        return ResponseEntity.ok(financialProfileService.getProfile(userId));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<FinancialProfileResponse> getProfile(@PathVariable Long userId) {
        ownershipChecker.verify(userId);
        return ResponseEntity.ok(financialProfileService.getProfile(userId));
}

    // 있으면 수정, 없으면 등록
    @PostMapping
    public ResponseEntity<FinancialProfileResponse> upsertProfile(
            @AuthenticationPrincipal Long userId,
            @RequestBody FinancialProfileRequest dto) {
        return ResponseEntity.ok(financialProfileService.upsertProfile(userId, dto));
    }
}