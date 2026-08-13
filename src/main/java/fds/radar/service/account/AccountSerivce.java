package fds.radar.service.account;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fds.radar.common.AccountStatus;
import fds.radar.common.AccountType;
import fds.radar.dto.account.AccountCreateRequest;
import fds.radar.dto.account.AccountLimitUpdateRequest;
import fds.radar.dto.account.AccountResponse;
import fds.radar.entity.account.Accounts;
import fds.radar.entity.user.Users;
import fds.radar.exception.BusinessException;
import fds.radar.exception.NotFoundException;
import fds.radar.repository.account.AccountRepository;
import fds.radar.repository.user.UserRepository;

@Service
public class AccountSerivce {
    
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final AccountNumberGenerator accountNumberGenerator;

    public AccountSerivce(AccountRepository accountRepository, UserRepository userRepository, AccountNumberGenerator accountNumberGenerator) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.accountNumberGenerator = accountNumberGenerator;
    }

    // 계좌 개설 - 잔액 0, 발번, status ACTIVE
    @Transactional
    public AccountResponse createAccount(Long userId, AccountCreateRequest request) {
        Users user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));

            Accounts account = Accounts.builder()
                .user(user)
                .accountNumber(accountNumberGenerator.generate())
                .accountType(AccountType.CHECKING)
                .balance(BigDecimal.ZERO)
                .dailyTransferLimit(request.getDailyTransferLimit())
                .accountStatus(AccountStatus.ACTIVE)
                .openedAt(LocalDateTime.now())
                .build();
            return AccountResponse.from(accountRepository.save(account));            
    }

    // 내 계좌 목록
    @Transactional(readOnly = true)
    public List<AccountResponse> getMyAccounts(Long userId) {
        return accountRepository.findByUser_UserId(userId).stream()
            .map(AccountResponse::from)
            .toList();
    }

    // 계좌 상세 - 본인 소유 검증
    @Transactional(readOnly = true)
    public AccountResponse getAccount(Long userId, Long accountId) {
        return AccountResponse.from(findOwened(userId, accountId));
    }

    // 본인 소유 검증 공통 - 없거나 남의 계좌일 경우 예외
    private Accounts findOwened(Long userId, Long accountId) {
        return accountRepository.findByAccountIdAndUser_UserId(accountId, userId)
            .orElseThrow(() -> new NotFoundException("게좌를 찾을 수 없습니다."));
    
    }

    // 일일 이체한도 변경
    @Transactional
    public AccountResponse updateLimit(Long userId, Long accountId, AccountLimitUpdateRequest request) {
        Accounts account = findOwened(userId, accountId);
        account.setDailyTransferLimit(request.getDailyTransferLimit());
        return AccountResponse.from(account); // 더블 체킹으로 자동 UPDATE
    }

    // 계좌 해지 - 잔액 0 확인 후 CLOSED
    @Transactional
    public void closeAccount(Long userId, Long accountId) {
        Accounts account = findOwened(userId, accountId);

        if(account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new BusinessException("잔액이 남아있어 해지할 수 없습니다.");
        }
        if(account.getAccountStatus() == AccountStatus.CLOSED) {
            throw new BusinessException("이미 해지된 계좌입니다.");
        }
        account.setAccountStatus(AccountStatus.CLOSED);
    }




}
 