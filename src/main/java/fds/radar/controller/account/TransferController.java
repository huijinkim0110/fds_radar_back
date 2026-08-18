package fds.radar.controller.account;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fds.radar.dto.account.TransferRequest; // 방금 만든 DTO 임포트
import fds.radar.exception.BusinessException;
import fds.radar.service.account.TransferRecipientService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/transfer")
public class TransferController {

    private final TransferRecipientService transferRecipientService;

    public TransferController(TransferRecipientService transferRecipientService) {
        this.transferRecipientService = transferRecipientService;
    }

    
    @PostMapping
public ResponseEntity<String> transfer(@RequestBody @Valid TransferRequest requestDto) {
    try {
        transferRecipientService.transfer(
            requestDto.getReceiverAccountNumber(), 
            requestDto.getAmount()
        );
        return ResponseEntity.ok("송금이 성공적으로 완료되었습니다.");
    } catch (IllegalArgumentException | BusinessException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
}