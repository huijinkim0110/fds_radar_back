package fds.radar.controller.finance;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fds.radar.dto.finance.LiabilityRequest;
import fds.radar.dto.finance.LiabilityResponse;
import fds.radar.service.finance.LiabilityService;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/liabilities")
@RequiredArgsConstructor
public class LiabilityController {
    
    private final LiabilityService liabilityService;

    // 부채 등록
    @PostMapping("/users/{userId}")
    public ResponseEntity<LiabilityResponse> create(
            @PathVariable Long userId,
            @RequestBody LiabilityRequest request) {

        LiabilityResponse response = 
                liabilityService.create(userId, request);

        return ResponseEntity.ok(response);
    }

    // 사용자 전체 부채 조회
    @GetMapping("/users/{userId}")
    public ResponseEntity<List<LiabilityResponse>> getLiabilities(
            @PathVariable Long userId) {

        List<LiabilityResponse> responses =
                liabilityService.getLiabilities(userId);

        return ResponseEntity.ok(responses);
    }
    
    // 부채 한 건 조회
    @GetMapping("/{liabilityId}")
    public ResponseEntity<LiabilityResponse> getLiability(
            @PathVariable Long liabilityId) {

        LiabilityResponse response = 
                liabilityService.getLiability(liabilityId);
        
        return ResponseEntity.ok(response);
    }
    
     // 부채 수정
    @PutMapping("/{liabilityId}")
    public ResponseEntity<LiabilityResponse> update(
            @PathVariable Long liabilityId,
            @RequestBody LiabilityRequest request) {

        LiabilityResponse response =
                liabilityService.update(liabilityId, request);

        return ResponseEntity.ok(response);
    }

    // 부채 삭제
    @DeleteMapping("/{liabilityId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long liabilityId) {

        liabilityService.delete(liabilityId);

        return ResponseEntity.noContent().build();
    }

    // 총 남은 부채 금액 조회
    @GetMapping("/users/{userId}/total")
    public ResponseEntity<Long> getTotalRemainingAmount(
            @PathVariable Long userId) {

        Long totalRemainingAmount =
                liabilityService.getTotalRemainingAmount(userId);

        return ResponseEntity.ok(totalRemainingAmount);
    }

    // DSR 계산
    @GetMapping("/users/{userId}/dsr")
    public ResponseEntity<Double> getDsr(
            @PathVariable Long userId,
            @RequestParam Long annualIncome) {

        Double dsr =
                liabilityService.calculateDsr(userId, annualIncome);

        return ResponseEntity.ok(dsr);
    }

}
