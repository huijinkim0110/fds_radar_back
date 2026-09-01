package fds.radar.controller.finance;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fds.radar.dto.financial.FinancialProfileRequest;
import fds.radar.dto.financial.FinancialProfileResponse;
import fds.radar.service.finance.FinancialProfileService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/financial-profiles")
@RequiredArgsConstructor
public class FinancialProfileController {

    private final FinancialProfileService financialProfileService;

    // 금융 프로필 존재 여부 확인
    // GET /financial-profiles/exists?userId=1
    @GetMapping("/exists")
    public ResponseEntity<Boolean> hasProfile(@RequestParam Long userId) {
        return ResponseEntity.ok(financialProfileService.hasProfile(userId));
    }

    @GetMapping
    public ResponseEntity<FinancialProfileResponse> getProfile(@RequestParam Long userId) {
        return ResponseEntity.ok(financialProfileService.getProfile(userId));
    }

    // 있으면 수정, 없으면 등록
    @PostMapping
    public ResponseEntity<FinancialProfileResponse> upsertProfile(@RequestBody FinancialProfileRequest dto) {
        return ResponseEntity.ok(financialProfileService.upsertProfile(dto));
    }
}