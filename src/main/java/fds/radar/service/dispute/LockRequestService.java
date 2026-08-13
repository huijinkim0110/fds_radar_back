package fds.radar.service.dispute;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fds.radar.common.AccountStatus;
import fds.radar.common.CardStatus;
import fds.radar.common.LockRequestStatus;
import fds.radar.common.RequestTargetType;
import fds.radar.dto.dispute.LockRequestCreateRequest;
import fds.radar.dto.dispute.LockRequestProcessRequest;
import fds.radar.dto.dispute.LockRequestResponse;
import fds.radar.entity.account.Accounts;
import fds.radar.entity.account.Cards;
import fds.radar.entity.dispute.LockRequests;
import fds.radar.entity.fraud.FraudCases;
import fds.radar.entity.transaction.Transactions;
import fds.radar.exception.BusinessException;
import fds.radar.exception.NotFoundException;
import fds.radar.repository.account.AccountRepository;
import fds.radar.repository.account.CardRepository;
import fds.radar.repository.fraud.FraudCaseRepository;
import fds.radar.repository.fraud.LockRequestRepository;

@Service
public class LockRequestService {
    
    private final LockRequestRepository lockRequestRepository;
    private final FraudCaseRepository fraudCaseRepository;
    private final CardRepository cardRepository;
    private final AccountRepository accountRepository;

    public LockRequestService(LockRequestRepository lockRequestRepository,
                              FraudCaseRepository fraudCaseRepository,
                              CardRepository cardRepository,
                              AccountRepository accountRepository) {
        this.lockRequestRepository = lockRequestRepository;
        this.fraudCaseRepository = fraudCaseRepository;
        this.cardRepository = cardRepository;
        this.accountRepository = accountRepository;
    }


    // 잠금 요청 생성 (fraud_case 기반)
    @Transactional
    public LockRequestResponse create(LockRequestCreateRequest request) {
        FraudCases fraudCase = fraudCaseRepository.findById(request.getFraudCaseId())
            .orElseThrow(() -> new NotFoundException("이상거래 사건을 찾을 수 없습니다."));
    
        // 같은 사건으로 미처리(RECEIVED) 요청 있으면 중복 방지
        boolean dup = lockRequestRepository.existsByFraudCase_FraudCaseIdAndRequestStatus(
            request.getFraudCaseId(), LockRequestStatus.RECEIVED);
            if(dup) {
                throw new BusinessException("이미 처리 대기중인 잠금 요청입니다.");
            }

            LockRequests lock = LockRequests.builder()
                .user(fraudCase.getUser())
                .fraudCase(fraudCase)
                .targetType(request.getTargetType())
                .requestReason(request.getRequestReason())
                .requestStatus(LockRequestStatus.RECEIVED)
                .requestedAt(LocalDateTime.now())
                .build();

        return LockRequestResponse.from(lockRequestRepository.save(lock));
    }

    // 잠금 요청 처리 (ADMIN 승인/반려)
    @Transactional
    public LockRequestResponse process(Long lockRequestId, LockRequestProcessRequest request) {
        LockRequests lock = lockRequestRepository.findById(lockRequestId)
            .orElseThrow(() -> new NotFoundException("잠금 요청을 찾을 수 없습니다."));

        if (lock.getRequestStatus() != LockRequestStatus.RECEIVED) {
            throw new BusinessException("이미 처리된 요청입니다.");
            }
        
            LockRequestStatus decision = request.getRequestStatus();
            // 승인이면 실제 대상 잠금
            if(decision == LockRequestStatus.COMPLETED) {
                applyLock(lock);
            }

            lock.setRequestStatus(decision);
            lock.setProcessedAt(LocalDateTime.now());
            return LockRequestResponse.from(lock);
    }

    // fraud_case -> transaction -> 대상 카드/계좌 잠금
    private void applyLock(LockRequests lock) {
        Transactions tx = lock.getFraudCase().getTransaction();

        if(lock.getTargetType() == RequestTargetType.CARD) {
            if(tx.getCards() == null) {
                throw new BusinessException("잠글 카드가 없는 거래입니다.");
            }
            Cards card = cardRepository.findByCardIdForUpdate(tx.getCards().getCardId())
                .orElseThrow(() -> new NotFoundException("카드를 찾을 수 없습니다."));
            card.setStatus(CardStatus.LOCKED);
        } else if (lock.getTargetType() == RequestTargetType.ACCOUNT) {
            if(tx.getAccount() == null) {
                throw new BusinessException("잠글 계좌가 없는 거래입니다.");
            }
            Accounts account = accountRepository.findByAccountIdForUpdate(tx.getAccount().getAccountId())
                .orElseThrow(() -> new NotFoundException("게좌를 찾을 수 없습니다."));
            account.setAccountStatus(AccountStatus.ACCOUNT_BLOCKED);
        }
    }

    // 조회
    @Transactional(readOnly = true)
    public List<LockRequestResponse> getMyLockRequests(Long userId) {
        return lockRequestRepository.findByUser_UserId(userId).stream()
            .map(LockRequestResponse::from)
            .toList();
    }

    @Transactional(readOnly = true)
    public LockRequestResponse getLockRequest(Long userId, Long lockRequestId) {
        LockRequests lock = lockRequestRepository
            .findByLockRequestIdAndUser_UserId(lockRequestId, userId)
            .orElseThrow(() -> new NotFoundException("잠금 요청을 찾을 수 없습니다."));
        return LockRequestResponse.from(lock);
    }

    // 관리자 처리 대기 목록
    @Transactional(readOnly = true)
    public List<LockRequestResponse> getReceivedRequests() {
        return lockRequestRepository.findByRequestStatus(LockRequestStatus.RECEIVED)
            .stream()
            .map(LockRequestResponse::from)
            .toList();
    }
}
