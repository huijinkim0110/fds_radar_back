package fds.radar.controller.finance;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fds.radar.dto.finance.AssetRequest;
import fds.radar.dto.finance.AssetResponse;
import fds.radar.service.finance.AssetService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;



@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
public class AssetController {
    
    private final AssetService assetService;

    // 자산 등록
    @PostMapping("/users/{userId}")
    public ResponseEntity<AssetResponse> create(
        @PathVariable Long userId,
        @RequestBody AssetRequest request) {

        AssetResponse response = 
                assetService.create(userId, request);

        return ResponseEntity.ok(response);

    }

    // 사용자의 전체 자산 조회
    @GetMapping("/users/{userId}")
    public ResponseEntity<List<AssetResponse>> getAssets(
            @PathVariable Long userId) {

        List<AssetResponse> responses = 
                assetService.getAssets(userId);
        
        return ResponseEntity.ok(responses);
    }

    // 자산 한 건 조회
    @GetMapping("/{assetId}")
    public ResponseEntity<AssetResponse> getAsset(
            @PathVariable Long assetId) {

        AssetResponse response = 
                assetService.getAsset(assetId);

        return ResponseEntity.ok(response);
    }
    
    // 자산 수정
    @PutMapping("/{assetId}")
    public ResponseEntity<AssetResponse> update(
            @PathVariable Long assetId,
            @RequestBody AssetRequest request) {

        AssetResponse response =
                assetService.update(assetId, request);

        return ResponseEntity.ok(response);
    }

    // 자산 삭제
    @DeleteMapping("/{assetId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long assetId) {

        assetService.delete(assetId);

        return ResponseEntity.noContent().build();

    }

    // 총자산 조회
    @GetMapping("/users/{userId}/total")
    public ResponseEntity<Long> getTotalAssets(
            @PathVariable Long userId) {

        Long totalAssets =
                assetService.getTotalAssets(userId);

        return ResponseEntity.ok(totalAssets);
    }
}
