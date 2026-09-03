package fds.radar.controller.finance;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fds.radar.dto.finance.InvestmentDiagnosisRequestDTO;
import fds.radar.entity.finance.InvestmentProfiles;
import fds.radar.service.finance.InvestmentProfileService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/investment-profiles")
@RequiredArgsConstructor
public class InvestmentProfileController {
    
    private final InvestmentProfileService investmentProfileService;

    // 투자성향 진단 - 설문 응답 제출
    @PostMapping
    public ResponseEntity<InvestmentProfiles> diagnose(@RequestBody InvestmentDiagnosisRequestDTO dto) {
        InvestmentProfiles result = investmentProfileService.diagnose(dto);
        return ResponseEntity.ok(result);
    }

    // 진단 이력 조회(마이페이지) - 기본 최신 3건, limit로 개수 조절 가능
    // GET /investment-profiles?userId=1&limit=3
    @GetMapping
    public ResponseEntity<List<InvestmentProfiles>> getRecentProfiles(
           @RequestParam Long userId,
           @RequestParam(defaultValue = "3") int limit) {
        
        List<InvestmentProfiles> result = investmentProfileService.getRecentProfiles(userId, limit);
        return ResponseEntity.ok(result);
    }

    // 가장 최근 진단 1건 조회(적합성 검사/추천 화면에서 현재 투자성향 보여줄 때 사용)
    // GET /investment-profiles/latest?userId=1
    @GetMapping("/latest")
    public ResponseEntity<InvestmentProfiles> getLatestProfile(@RequestParam Long userId) {
        InvestmentProfiles result = investmentProfileService.getLatestProfile(userId);
        return ResponseEntity.ok(result);
    }

    // 진단 이력 존재 여부 확인 - "진단 먼저 받기" 안내가 필요한지 프론트에서 판단할 때 사용
    // GET /investment-profiles/exists?userId=1
    @GetMapping("/exists")
    public ResponseEntity<Boolean> hasDiagnosisHistory(@RequestParam Long userId) {
        boolean result = investmentProfileService.hasDiagnosisHistory(userId);
        return ResponseEntity.ok(result);
    }

    // 비로그인 사용자용 - 설문 응답 제출, 점수 계산만 하고 DB 저장은 하지 않음(체험용)
    // POST /investment-profiles/preview
    @PostMapping("/preview")
    public ResponseEntity<InvestmentProfiles> previewDiagnose(@RequestBody InvestmentDiagnosisRequestDTO dto) {
        InvestmentProfiles result = investmentProfileService.previewDiagnose(dto);
        return ResponseEntity.ok(result);
    }
}
