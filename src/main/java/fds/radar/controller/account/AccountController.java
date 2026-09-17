package fds.radar.controller.account;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fds.radar.dto.account.AccountCreateRequest;
import fds.radar.dto.account.AccountLimitUpdateRequest;
import fds.radar.dto.account.AccountResponse;
import fds.radar.service.account.AccountSerivce;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    
    private final AccountSerivce accountSerivce;

    public AccountController(AccountSerivce accountSerivce) {
        this.accountSerivce = accountSerivce;
    }

    @PostMapping
    public AccountResponse create(@AuthenticationPrincipal Long userId,
                                @Valid @RequestBody AccountCreateRequest request) {
        return accountSerivce.createAccount(userId, request);
    }

    // 내 계좌 목록
    @GetMapping
    public List<AccountResponse> getMyAccounts(@AuthenticationPrincipal Long userId) {
        return accountSerivce.getMyAccounts(userId);
    }

    // 계좌 상세 
    @GetMapping("/{accountId}")
    public AccountResponse getAccount(@AuthenticationPrincipal Long userId,
                                    @PathVariable Long accountId) {
        return accountSerivce.getAccount(userId, accountId);
    }

    // 일일 이체한도 변경
    @PatchMapping("/{accountId}/limit")
    public AccountResponse updateLimit(@AuthenticationPrincipal Long userId,
                                        @PathVariable Long accountId,
                                        @Valid @RequestBody AccountLimitUpdateRequest request) {
        return accountSerivce.updateLimit(userId, accountId, request);
    }

    // 계좌 해지
    @DeleteMapping("/{accountId}")
    public void closeAccount(@AuthenticationPrincipal Long userId,
                            @PathVariable Long accountId) {
        accountSerivce.closeAccount(userId, accountId);
                            }
}

