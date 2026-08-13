package fds.radar.service.transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.transaction.annotation.Transactional;

import fds.radar.common.AccountStatus;
import fds.radar.common.CardStatus;
import fds.radar.common.TransactionStatus;
import fds.radar.common.TransactionType;
import fds.radar.dto.transaction.PaymentRequest;
import fds.radar.dto.transaction.TransactionResponse;
import fds.radar.dto.transaction.TransferRequest;
import fds.radar.entity.account.Accounts;
import fds.radar.entity.account.Cards;
import fds.radar.entity.account.TransferRecipients;
import fds.radar.entity.transaction.Merchants;
import fds.radar.entity.transaction.Transactions;
import fds.radar.entity.user.Users;
import fds.radar.exception.BusinessException;
import fds.radar.exception.NotFoundException;
import fds.radar.repository.account.AccountRepository;
import fds.radar.repository.account.CardRepository;
import fds.radar.repository.account.TransferRepository;
import fds.radar.repository.transaction.MerchanstRepository;
import fds.radar.repository.transaction.TransactionRepository;
import fds.radar.repository.user.UserRepository;
import lombok.var;

public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CardRepository cardRepository;
    private final MerchanstRepository merchanstRepository;
    private final TransferRepository transferRepository;
    private final UserRepository userRepository;


    public TransactionService(TransactionRepository transactionRepository,
                              AccountRepository accountRepository,
                              CardRepository cardRepository,
                              MerchanstRepository merchanstRepository,
                              TransferRepository transferRepository,
                              UserRepository userRepository
    ) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.cardRepository = cardRepository;
        this.merchanstRepository = merchanstRepository;
        this.transferRepository = transferRepository;
        this.userRepository = userRepository;
    
    }  

    // 카드 결제
    @Transactional
    public TransactionResponse pay(Long userId, PaymentRequest request) {
        // 1. 멱등키 중복체크 - 이미 처리된 요청이면 기존 거래 반환
        var existing = transactionRepository.findByIdemotencyKey(request.getIdempotencyKey());
        if(existing.isPresent()) {
            return TransactionResponse.from(existing.get());
        }

        Users user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));
        
        // 2. 카드 비관적 락으로 조회 (사용가능한도 동시 차감 방지)
        Cards card = cardRepository.findByCardIdForUpdate(request.getCardId())
            .orElseThrow(() -> new NotFoundException("카드를 찾을 수 없습니다."));

        // 본인 카드 검증
        if(!card.getUser().getUserId().equals(userId)) {
            throw new NotFoundException("카드를 찾을 수 없습니다.");
        }
        if(card.getStatus() != CardStatus.ACTIVE) {
            throw new BusinessException("사용할 수 없는 카드입니다.");
        }

        Merchants merchants = merchanstRepository.findById(request.getMerchantId())
            .orElseThrow(() -> new NotFoundException("가맹점을 찾을 수 없습니다."));

        BigDecimal amount = request.getAmount();

        // 3. 사용가능한도 확인. 차감
        if(card.getAvailableLimit().compareTo(amount) < 0) {
            throw new BusinessException("사용가능한도를 초과했습니다.");
        }

        card.setAvailableLimit(card.getAvailableLimit().subtract(amount));

        // 4. 거래 기록 
        LocalDateTime now = LocalDateTime.now();
        Transactions tx = Transactions.builder()
                .users(user)
                .cards(card)
                .merchant(merchants)
                .transactionType(TransactionType.CARD_PAYMENT)
                .amount(amount)
                .transactionChannel(request.getChannel())
                .countryCode(merchants.getCountryCode())
                .region(merchants.getRegion())
                .transactionStatus(TransactionStatus.APPROVED)
                .idempotencyKey(request.getIdempotencyKey())
                .occurredAt(now)
                .approvedAt(now)
                .build();
        
        return TransactionResponse.from(transactionRepository.save(tx));

    }


    // ===== 계좌이체 =====
    @Transactional
    public TransactionResponse transfer(Long userId, TransferRequest request) {
        var existing = transactionRepository.findByIdemotencyKey(request.getIdempotencyKey());
        if (existing.isPresent()) {
            return TransactionResponse.from(existing.get());
        }

        Users user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));
    
        // 출금계좌 비관적 락
        Accounts account = accountRepository.findByAccountIdForUpdate(request.getFromAccountId())
            .orElseThrow(() -> new NotFoundException("출금 계좌를 찾을 수 없습니다."));
        
        if (!account.getUser().getUserId().equals(userId)) {
            throw new NotFoundException("출금 계좌를 찾을 수 없습니다.");
        }
        if (account.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new BusinessException("출금할 수 없는 계좌입니다."); // 잠금/해지 상태
        }

        TransferRecipients recipients = transactionRepository
            .findByRecipientIdAndUser_UserID(request.getRecipientId());
            .orElseThrow(() -> new NotFoundException("수취인을 찾을 수 없습니다."));
        
        BigDecimal amount = request.getAmount();

        // 잔액. 일일한도 확인. 차감
        if(account.getBalance().compareTo(amount) < 0) {
            throw new BusinessException("잔액이 부족합니다.");
        }
        if(account.getDailyTransferLimit().compareTo(amount) < 0) {
            throw new BusinessException("일일 이체한도를 초과했습니다. ")
        }


        }

}
