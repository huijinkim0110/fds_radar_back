package fds.radar.controller.finance;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fds.radar.dto.financial.FinancialProfileRequest;
import fds.radar.dto.financial.FinancialProfileResponse;
import fds.radar.service.finance.FinancialProfileService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequestMapping("/apl/financial-profiles")
@RequiredArgsConstructor
public class FinancialProfileController {
    
    private final FinancialProfileService financialProfileService;

    // 금융 프로필 등록
    @PostMapping("/{userId}")
    public ResponseEntity<FinancialProfileResponse> create(
            @PathVariable Long userId,
            @RequestBody FinancialProfileRequest request) {

        FinancialProfileResponse response = 
                financialProfileService.create(userId, request);

        return ResponseEntity.ok(response);
    }
    
    // 금융 프로필 조희
    @GetMapping("/{userId}")
    public ResponseEntity<FinancialProfileResponse> getProfile(
            @PathVariable Long userId) {
        
        FinancialProfileResponse response =
                financialProfileService.getProfile(userId);

        return ResponseEntity.ok(response);
    }

    // 금융 프로필 수정
    @PutMapping("/{userId}")
    public ResponseEntity<FinancialProfileResponse> update(
            @PathVariable Long userId,
            @RequestBody FinancialProfileRequest request) {

        FinancialProfileResponse response =
                financialProfileService.update(userId, request);
            
        return ResponseEntity.ok(response);
    }
}