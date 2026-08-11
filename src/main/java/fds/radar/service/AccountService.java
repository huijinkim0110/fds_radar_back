package fds.radar.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import fds.radar.common.AccountStatus;
import fds.radar.common.AccountType;
import fds.radar.dto.AccountCreateRequest;
import fds.radar.dto.AccountLimitUpdateRequest;
import fds.radar.dto.AccountResponse;
import fds.radar.entity.account.Accounts;
import fds.radar.entity.user.Users;
import fds.radar.exception.BusinessException;
import fds.radar.exception.NotFoundException;
import fds.radar.repository.AccountRepository;
import fds.radar.repository.user.UserRepository;


public class AccountService {
    
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final AccountNumberGenerator accountNumberGenerator;

    public AccountService(AccountRepository accountRepository,
                          UserRepository userRepository,
                          AccountNumberGenerator accountNumberGenerator) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.accountNumberGenerator = accountNumberGenerator;
    }

    // 계조 개설 - 잔액 0, 발번, status ACTIVE
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
                .openedAt(java.time.LocalDateTime.now())
                .build();
    return AccountResponse.from(accountRepository.save(account));
  }

  // 내 계좌 목록
  @Transactional(readOnly = true)
  public List<AccountResponse> getMyAccounts(Long userId) {
    return accountRepository.findByUserId(userId).stream()
            .map(AccountResponse::from)
            .toList();
    }

    // 계좌 상세 - 본인 소유 검증
    @Transactional(readOnly = true)
    public AccountResponse getAccount(Long userId, Long accountId) {
        return AccountResponse.from(findOwned(userId, accountId));
    }

    // 일일 이체한도 변경
    @Transactional
    public AccountResponse updateLimit(Long userId, Long accountId, AccountLimitUpdateRequest request) {
        Accounts account = findOwned(userId, accountId);
        account.setDailyTransferLimit(request.getDailyTransferLimit());
        return AccountResponse.from(account); // 더블 체킹으로 자동 UPDATE
    }

    // 계좌 해지 - 잔액 0 확인 후 CLOSED 
    @Transactional
    public void closeAccount(Long userId, Long accountId) {
        Accounts account = findOwned(userId, accountId);

        if(account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new BusinessException("잔액이 남아있어 해지할 수 없습니다.");
        }
        if(account.getAccountStatus() == AccountStatus.CLOSED) {
            throw new BusinessException("이미 해지된 계좌입니다.");
        }
        account.setAccountStatus(AccountStatus.CLOSED);
    }

    // 본인 소유 검증 공통 - 없거나 남의 계좌면 예외
    private Accounts findOwned(Long userId, Long accountId) {
        return accountRepository.findByIdAndUserId(accountId, userId)
                .orElseThrow(() -> new NotFoundException("계좌를 찾을 수 없습니다."));

    }
}
