package fds.radar.controller.account;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fds.radar.dto.account.CardCreateRequest;
import fds.radar.dto.account.CardLimitUpdateRequest;
import fds.radar.dto.account.CardResponse;
import fds.radar.dto.account.CardStatusUpdateRequest;
import fds.radar.service.account.CardService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cards")
public class CardController {
    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    // 카드 발급 API
    @PostMapping
    public ResponseEntity<CardResponse> createCard(
        @Valid @RequestBody CardCreateRequest request,
        // TODO: 로그인한 유저 정보 추출 (예: @AuthenticationPrincipal 또는 SecurityContextHolder)
            @RequestParam Long userId // 임시로 파라미터나 시큐리티로 처리
    ) {
        CardResponse response = cardService.createCard(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 2. 내 카드 목록 조회 API
    @GetMapping
    public ResponseEntity<List<CardResponse>> getMyCards (
        @RequestParam Long userId
    ) {
        List<CardResponse> cards = cardService.getCardsByUserId(userId);
        return ResponseEntity.ok(cards);
    }

    // 3. 카드 이용한도 변경 API
    @PatchMapping("/{cardId}/limit")
    public ResponseEntity<Void> updateCardLimit(
        @PathVariable Long cardId,
        @Valid @RequestBody CardLimitUpdateRequest request
    ) {
        cardService.updateCardLimit(cardId, request.getCreditLimit());
        return ResponseEntity.ok().build();
    }

    // 카드 상태 변경 API
    @PatchMapping("/{cardId}/status")
    public ResponseEntity<Void> updateCardStatus(
        @PathVariable Long cardId,
        @Valid @RequestBody CardStatusUpdateRequest request
    ) {
        cardService.updateCardStatus(cardId, request.geStatus());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{cardId}/pay")
    public ResponseEntity<Void> pay(
        @PathVariable Long cardId,
        @RequestParam BigDecimal amount 
    ) {
        cardService.payWithCard(cardId, amount);
        return ResponseEntity.ok().build();
    }
}
