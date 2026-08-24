package fds.radar.controller.finance;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fds.radar.dto.financial.FinancialInstitutionResponse;
import fds.radar.dto.financial.FinanciallnstitutionRequest;
import fds.radar.service.finance.FinancialInstitutionService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/financial-institutions")
@RequiredArgsConstructor
public class FinancialInstitutionController {

    private final FinancialInstitutionService financialInstitutionService;

    // 금융기관 등록
    @PostMapping
    public ResponseEntity<FinancialInstitutionResponse> create(
            @RequestBody FinancialInstitutionResponse request) {

        FinancialInstitutionResponse response =
                financialInstitutionService.create(request);

        return ResponseEntity.ok(response);
    }

    // 금융기관 전체 조회
    @GetMapping
    public ResponseEntity<List<FinancialInstitutionResponse>> getInstitutions() {

        List<FinancialInstitutionResponse> responses =
                financialInstitutionService.getInstitution();

        return ResponseEntity.ok(responses);
    }

    // 금융기관 한 건 조회
    @GetMapping("/{institutionId}")
    public ResponseEntity<FinancialInstitutionResponse> getInstitution(
            @PathVariable Long institutionId) {

        FinancialInstitutionResponse response =
                financialInstitutionService.getInstitution(institutionId);

        return ResponseEntity.ok(response);
    }

    // 금융기관 수정
    @PutMapping("/{institutionId}")
    public ResponseEntity<FinancialInstitutionResponse> update(
            @PathVariable Long institutionId,
            @RequestBody FinanciallnstitutionRequest request) {

        FinancialInstitutionResponse response =
                financialInstitutionService.update(institutionId, request);

        return ResponseEntity.ok(response);
    }
}