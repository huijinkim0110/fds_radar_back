package fds.radar.service.account;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fds.radar.common.CardStatus;
import fds.radar.dto.account.TransferRecipientResponse;
import fds.radar.dto.account.TransferRecipientsCreateRequest;
import fds.radar.entity.account.Accounts;
import fds.radar.entity.account.Cards;
import fds.radar.entity.account.Institutions;
import fds.radar.entity.account.TransferRecipients;
import fds.radar.entity.user.Users;
import fds.radar.exception.BusinessException;
import fds.radar.exception.NotFoundException;
import fds.radar.repository.account.AccountRepository;
import fds.radar.repository.account.CardRepository;
import fds.radar.repository.account.InstitutionRepository;
import fds.radar.repository.account.TransferRepository;
import fds.radar.repository.user.UserRepository;

@Service
public class TransferRecipientService {

    private final TransferRepository transferRepository;
    private final TransferRepository recipientRepository;
    private final UserRepository userRepository;
    private final InstitutionRepository institutionRepository;
    private final AccountRepository accountRepository;
    private final CardRepository cardRepository;
    private final CardService cardService;

    public TransferRecipientService(
            TransferRepository transferRepository,
            TransferRepository recipientRepository,
            UserRepository userRepository,
            InstitutionRepository institutionRepository,
            AccountRepository accountRepository,
            CardRepository cardRepository,
            CardService cardService) {

        this.transferRepository = transferRepository;
        this.recipientRepository = recipientRepository;
        this.userRepository = userRepository;
        this.institutionRepository = institutionRepository;
        this.accountRepository = accountRepository;
        this.cardRepository = cardRepository;
        this.cardService = cardService;
    }

    // 수취인 저장
    @Transactional
    public TransferRecipientResponse save(
            Long userId,
            TransferRecipientsCreateRequest request) {

        // 같은 기관 + 계좌 중복 방지
        boolean dup =
            transferRepository.existsByUser_UserIdAndInstitution_InstitutionIdAndAccountNumber(
                userId,
                request.getInstitutionId(),
                request.getAccountNumber()
            );

        if (dup) {
            throw new BusinessException("이미 저장된 수취인입니다.");
        }

        Users user = userRepository.findById(userId)
            .orElseThrow(() ->
                new NotFoundException("사용자를 찾을 수 없습니다."));

        Institutions institution =
            institutionRepository.findById(request.getInstitutionId())
                .orElseThrow(() ->
                    new BusinessException("금융기관을 찾을 수 없습니다."));

        TransferRecipients transferRecipients =
            TransferRecipients.builder()
                .user(user)
                .institution(institution)
                .recipientName(request.getRecipientName())
                .accountNumber(request.getAccountNumber())
                .isRegistered(true)
                .firstTransferAt(null)
                .lastTransferAt(null)
                .build();

        return TransferRecipientResponse.from(
            transferRepository.save(transferRecipients)
        );
    }

    // 내 수취인 목록
    @Transactional(readOnly = true)
    public List<TransferRecipientResponse> getMyRecipient(Long userId) {

        return recipientRepository.findByUser_UserId(userId)
            .stream()
            .map(TransferRecipientResponse::from)
            .toList();
    }

    // 수취인 삭제
    @Transactional
    public void delete(Long userId, Long recipientId) {

        TransferRecipients recipients =
            recipientRepository
                .findByRecipientIdAndUser_UserId(recipientId, userId)
                .orElseThrow(() ->
                    new NotFoundException("수취인을 찾을 수 없습니다."));

        recipientRepository.delete(recipients);
    }

    // =========================================================
    // 송금
    // cardId == null → 계좌 송금
    // cardId != null → 카드 송금
    // =========================================================
    @Transactional
    public void transfer(
            Long cardId,
            String receiverAccountNumber,
            Long amount) {

        // TODO: 테스트용 로그인 유저
        // 추후 SecurityContextHolder로 변경
        String currentEmail = "test@test.com";

        Users currentUser = userRepository.findByEmail(currentEmail)
            .orElseThrow(() ->
                new NotFoundException("로그인한 유저를 찾을 수 없습니다."));

        BigDecimal transferAmount = BigDecimal.valueOf(amount);

        // -----------------------------------------------------
        // 받는 계좌 확인
        // -----------------------------------------------------
        Accounts receiverAccount =
            accountRepository.findByAccountNumber(receiverAccountNumber)
                .orElseThrow(() ->
                    new NotFoundException("받는 계좌를 찾을 수 없습니다."));

        // =====================================================
        // 카드 송금
        // =====================================================
        if (cardId != null) {

            // 카드 ID + 현재 사용자 ID로 본인 카드인지 확인
            Cards card =
                cardRepository.findByCardIdAndUser_UserId(
                    cardId,
                    currentUser.getUserId()
                )
                .orElseThrow(() ->
                    new NotFoundException("카드를 찾을 수 없습니다."));

            // 카드 상태 확인
            if (card.getStatus() != CardStatus.ACTIVE) {
                throw new BusinessException("사용할 수 없는 카드입니다.");
            }

            // 카드 이용가능한도 차감
            cardService.payWithCard(
                cardId,
                transferAmount
            );

            // 받는 계좌에 송금액 입금
            receiverAccount.setBalance(
                receiverAccount.getBalance()
                    .add(transferAmount)
            );

            return;
        }

        // =====================================================
        // 기존 계좌 송금
        // =====================================================

        List<Accounts> senderAccounts =
            accountRepository.findByUser_UserId(
                currentUser.getUserId()
            );

        if (senderAccounts == null || senderAccounts.isEmpty()) {
            throw new NotFoundException("등록된 계좌를 찾을 수 없습니다.");
        }

        // 현재는 첫 번째 계좌를 송금 계좌로 사용
        Accounts senderAccount = senderAccounts.get(0);

        // 잔액 확인
        if (senderAccount.getBalance().compareTo(transferAmount) < 0) {
            throw new BusinessException("잔액이 부족합니다.");
        }

        // 보내는 계좌 잔액 차감
        senderAccount.setBalance(
            senderAccount.getBalance()
                .subtract(transferAmount)
        );

        // 받는 계좌 잔액 증가
        receiverAccount.setBalance(
            receiverAccount.getBalance()
                .add(transferAmount)
        );
    }
}