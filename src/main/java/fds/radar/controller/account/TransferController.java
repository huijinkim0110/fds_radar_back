package fds.radar.controller.account;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import fds.radar.dto.account.TransferRecipientsCreateRequest;
import fds.radar.dto.account.TransferRecipientResponse;
import fds.radar.service.account.TransferRecipientService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/recipients")
@CrossOrigin(origins = "*")
public class TransferController {

    private final TransferRecipientService transferRecipientService;

    public TransferController(TransferRecipientService transferRecipientService) {
        this.transferRecipientService = transferRecipientService;
    }

    // 수취인 저장
    @PostMapping
    public ResponseEntity<TransferRecipientResponse> save(
            @RequestParam Long userId,
            @Valid @RequestBody TransferRecipientsCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(transferRecipientService.save(userId, request));
    }

    // 내 수취인 목록
    @GetMapping
    public ResponseEntity<List<TransferRecipientResponse>> getMyRecipients(
            @RequestParam Long userId) {
        return ResponseEntity.ok(transferRecipientService.getMyRecipient(userId));
    }

    // 수취인 삭제
    @DeleteMapping("/{recipientId}")
    public ResponseEntity<Void> delete(
            @RequestParam Long userId,
            @PathVariable Long recipientId) {
        transferRecipientService.delete(userId, recipientId);
        return ResponseEntity.ok().build();
    }
}