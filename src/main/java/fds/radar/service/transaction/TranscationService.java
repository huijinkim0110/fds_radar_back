package fds.radar.service.transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;    
import org.springframework.transaction.annotation.Transactional;

import fds.radar.common.AccountStatus;
import fds.radar.common.CardStatus;
import fds.radar.common.TransactionStatus;
import fds.radar.common.TransactionType;
import fds.radar.dto.transaction.PaymentRequest;
import fds.radar.dto.transaction.TransactionResponse;
import fds.radar.dto.transaction.TransactionStatusUpdateRequest;
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

@Service
public class TranscationService {
    
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CardRepository cardRepository;
    private final MerchanstRepository merchanstRepository;
    private final TransferRepository transferRepository;
    private final UserRepository userRepository;

    public TranscationService(TransactionRepository transactionRepository,
                              AccountRepository accountRepository,
                              CardRepository cardRepository,
                              MerchanstRepository merchanstRepository,
                              TransferRepository transferRepository,
                              UserRepository userRepository) {
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
        var existing = transactionRepository.findByIdempotencyKey(request.getIdempotencyKey());
        if(existing.isPresent()) {
            return TransactionResponse.from(existing.get());
        }

        Users user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));
        
        Cards card = cardRepository.findByCardIdForUpdate(request.getCardId())
            .orElseThrow(() -> new NotFoundException("카드를 찾을 수 없습니다."));
        if(!card.getUser().getUserId().equals(userId)) {
            throw new NotFoundException("카드를 찾을 수 없습니다.");
        }
        if(card.getStatus() != CardStatus.ACTIVE) {
            throw new BusinessException("사용할 수 없는 카드입니다.");
        }

        Merchants merchants = merchanstRepository.findById(request.getMerchantId())
            .orElseThrow(() -> new NotFoundException("가맹점을 찾을 수 없습니다."));

        BigDecimal amount = request.getAmount();
        if(card.getAvailableLimit().compareTo(amount) < 0) {
            throw new BusinessException("사용가능 한도를 초과했습니다.");
        }
        card.setAvailableLimit(card.getAvailableLimit().subtract(amount));

            LocalDateTime now = LocalDateTime.now();
        Transactions tx = Transactions.builder()
                .user(user)
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
        var existing = transactionRepository.findByIdempotencyKey(request.getIdempotencyKey());
        if (existing.isPresent()) {
            return TransactionResponse.from(existing.get());
        }

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));

        Accounts account = accountRepository.findByAccountIdForUpdate(request.getFromAccountId())
                .orElseThrow(() -> new NotFoundException("출금 계좌를 찾을 수 없습니다."));
        if (!account.getUser().getUserId().equals(userId)) {
            throw new NotFoundException("출금 계좌를 찾을 수 없습니다.");
        }
        if (account.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new BusinessException("출금할 수 없는 계좌입니다.");
        }

        TransferRecipients recipient = transferRepository
                .findByRecipientIdAndUser_UserId(request.getRecipientId(), userId)
                .orElseThrow(() -> new NotFoundException("수취인을 찾을 수 없습니다."));

        BigDecimal amount = request.getAmount();
        if (account.getBalance().compareTo(amount) < 0) {
            throw new BusinessException("잔액이 부족합니다.");
        }
        if (account.getDailyTransferLimit().compareTo(amount) < 0) {
            throw new BusinessException("일일 이체한도를 초과했습니다.");
        }
        account.setBalance(account.getBalance().subtract(amount));

        LocalDateTime now = LocalDateTime.now();
        Transactions tx = Transactions.builder()
                .user(user)
                .account(account)
                .recipient(recipient)
                .transactionType(TransactionType.ACCOUNT_TRANSFER)
                .amount(amount)
                .transactionChannel(request.getChannel())
                .countryCode("KR")
                .region("UNKNOWN")
                .transactionStatus(TransactionStatus.APPROVED)
                .idempotencyKey(request.getIdempotencyKey())
                .occurredAt(now)
                .approvedAt(now)
                .build();

        return TransactionResponse.from(transactionRepository.save(tx));
    }

    // ===== 상태 변경 (취소 시 원복) =====
    @Transactional
    public TransactionResponse updateStatus(Long txId, TransactionStatusUpdateRequest request) {
        Transactions tx = transactionRepository.findById(txId)
                .orElseThrow(() -> new NotFoundException("거래를 찾을 수 없습니다."));

        TransactionStatus target = request.getStatus();
        if (target == TransactionStatus.CANCELED
                && tx.getTransactionStatus() != TransactionStatus.CANCELED) {
            restore(tx);
        }
        tx.setTransactionStatus(target);
        return TransactionResponse.from(tx);
    }

    private void restore(Transactions tx) {
        BigDecimal amount = tx.getAmount();
        if (tx.getTransactionType() == TransactionType.CARD_PAYMENT && tx.getCards() != null) {
            Cards card = cardRepository.findByCardIdForUpdate(tx.getCards().getCardId())
                    .orElseThrow(() -> new NotFoundException("카드를 찾을 수 없습니다."));
            card.setAvailableLimit(card.getAvailableLimit().add(amount));
        } else if (tx.getTransactionType() == TransactionType.ACCOUNT_TRANSFER && tx.getAccount() != null) {
            Accounts account = accountRepository.findByAccountIdForUpdate(tx.getAccount().getAccountId())
                    .orElseThrow(() -> new NotFoundException("계좌를 찾을 수 없습니다."));
            account.setBalance(account.getBalance().add(amount));
        }
    }

    // ===== 조회 =====
    @Transactional(readOnly = true)
    public Page<TransactionResponse> getMyTransactions(Long userId, Pageable pageable) {
        return transactionRepository.findByUser_UserId(userId, pageable)
                .map(TransactionResponse::from);
    }

    @Transactional(readOnly = true)
    public TransactionResponse getTransaction(Long userId, Long txId) {
        Transactions tx = transactionRepository.findByTransactionIdAndUser_UserId(txId, userId)
                .orElseThrow(() -> new NotFoundException("거래를 찾을 수 없습니다."));
        return TransactionResponse.from(tx);
    }
}
