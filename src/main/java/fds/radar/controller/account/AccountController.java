package fds.radar.controller.account;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fds.radar.dto.account.AccountCreateRequest;
import fds.radar.dto.account.AccountLimitUpdateRequest;
import fds.radar.dto.account.AccountResponse;
import fds.radar.service.account.AccountSerivce;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/accounts")
@CrossOrigin(origins = "*") /// 테스트용 - 프로트에서의 호출 허용
public class AccountController {
    
    private final AccountSerivce accountSerivce;

    public AccountController(AccountSerivce accountSerivce) {
        this.accountSerivce = accountSerivce;
    }

    // TODO 계좌 개설 - userId는 테스트용 파라미터 (후에 JWT를 받아와서 변경)
    @PostMapping
    public AccountResponse create(@RequestParam Long userId,
                                @Valid @RequestBody AccountCreateRequest request) {
        return accountSerivce.createAccount(userId, request);
    }

    // 내 계좌 목록
    @GetMapping
    public List<AccountResponse> getMyAccounts(@RequestParam Long userId) {
        return accountSerivce.getMyAccounts(userId);
    }

    // 계좌 상세 
    @GetMapping("/{accountId}")
    public AccountResponse getAccount(@RequestParam Long userId,
                                    @PathVariable Long accountId) {
        return accountSerivce.getAccount(userId, accountId);
    }

    // 일일 이체한도 변경
    @PatchMapping("/{accountId}/limit")
    public AccountResponse updateLimit(@RequestParam Long userId,
                                        @PathVariable Long accountId,
                                        @Valid @RequestBody AccountLimitUpdateRequest request) {
        return accountSerivce.updateLimit(userId, accountId, request);
    }

    // 계좌 해지
    @DeleteMapping("/{accountId}")
    public void closeAccount(@RequestParam Long userId,
                            @PathVariable Long accountId) {
        accountSerivce.closeAccount(userId, accountId);
                            }
}

