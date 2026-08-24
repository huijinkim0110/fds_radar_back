// TransactionController
package fds.radar.controller.transaction;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import fds.radar.dto.transaction.PaymentRequest;
import fds.radar.dto.transaction.TransactionResponse;
import fds.radar.dto.transaction.TransactionStatusUpdateRequest;
import fds.radar.dto.transaction.TransferRequest;
import fds.radar.service.transaction.TransactionService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "*")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // 카드 결제
    @PostMapping("/payment")
    public ResponseEntity<TransactionResponse> pay(
            @RequestParam Long userId,
            @Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(transactionService.pay(userId, request));
    }

    // 계좌 이체
    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponse> transfer(
            @RequestParam Long userId,
            @Valid @RequestBody TransferRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(transactionService.transfer(userId, request));
    }

    // 내 거래내역
    @GetMapping
    public ResponseEntity<Page<TransactionResponse>> getMyTransactions(
            @RequestParam Long userId,
            Pageable pageable) {
        return ResponseEntity.ok(transactionService.getMyTransactions(userId, pageable));
    }

    // 거래 상세
    @GetMapping("/{txId}")
    public ResponseEntity<TransactionResponse> getTransaction(
            @RequestParam Long userId,
            @PathVariable Long txId) {
        return ResponseEntity.ok(transactionService.getTransaction(userId, txId));
    }

    // 거래 상태 변경 (ADMIN)
    @PatchMapping("/{txId}/status")
    public ResponseEntity<TransactionResponse> updateStatus(
            @PathVariable Long txId,
            @Valid @RequestBody TransactionStatusUpdateRequest request) {
        return ResponseEntity.ok(transactionService.updateStatus(txId, request));
    }
}