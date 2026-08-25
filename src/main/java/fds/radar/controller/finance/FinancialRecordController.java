package fds.radar.controller.finance;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fds.radar.dto.financial.FinancialRecordRequest;
import fds.radar.dto.financial.FinancialRecordResponse;
import fds.radar.service.finance.FinancialRecordSerive;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/financial-records")
@RequiredArgsConstructor
public class FinancialRecordController {
    
    private final FinancialRecordSerive financialRecordSerive;

    // 수입/지출 기록 등록
    @PostMapping("/users/{userId}")
    public ResponseEntity<FinancialRecordResponse> create(
            @PathVariable Long userId,
            @RequestBody FinancialRecordRequest request) {

        FinancialRecordResponse response = 
                financialRecordSerive.create(userId, request);
    
        return ResponseEntity.ok(response);
   }

   // 사용자의 전체 수입/지출 기록 조회
   @GetMapping("/users/{userId}")
   public ResponseEntity<List<FinancialRecordResponse>> getRecords(
            @PathVariable Long userId) {

        List<FinancialRecordResponse> response =
                financialRecordSerive.getRecords(userId);
            
        return ResponseEntity.ok(response);
            
    }

    // 수입/지출 기록 수정
    @PutMapping("/{recordId}")
    public ResponseEntity<FinancialRecordResponse> update(
            @PathVariable Long recordId,
            @RequestBody FinancialRecordRequest request) {

        FinancialRecordResponse response =
                financialRecordSerive.update(recordId, request);
        
        return ResponseEntity.ok(response);
    }

    // 수입/지출 기록 삭제
    @DeleteMapping("/{recordId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long recordId) {

        financialRecordSerive.delete(recordId);

        return ResponseEntity.noContent().build();
    }
}
