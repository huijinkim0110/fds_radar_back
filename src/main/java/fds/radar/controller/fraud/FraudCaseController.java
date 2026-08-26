package fds.radar.controller.fraud;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fds.radar.dto.fraud.FraudConfirmationRequest;
import fds.radar.service.fraud.FraudCaseService;
import lombok.RequiredArgsConstructor;

/**
 * 일반 사용자용 이상거래 사건 API.
 * 관리자용(FraudCaseAdminController)과 경로/권한을 분리해두기 위해 별도 컨트롤러로 작성.
 * 지금은 "본인거래 확인" 기능 하나뿐이지만, 사용자 쪽 기능이 늘어나면 여기에 추가.
 */
@RestController
@RequestMapping("/api/fraud-cases")
@RequiredArgsConstructor
public class FraudCaseController {

    private final FraudCaseService fraudCaseService;

    // 6차: 사용자가 본인거래 여부(MINE/NOT_MINE/UNCONFIRMED)를 직접 응답
    @PatchMapping("/{fraudCaseId}/confirmation")
    public ResponseEntity<Void> updateConfirmation(@PathVariable Long fraudCaseId,
                                                     @RequestBody FraudConfirmationRequest request) {
        fraudCaseService.updateConfirmation(fraudCaseId, request);
        return ResponseEntity.ok().build();
    }
}