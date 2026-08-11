package fds.radar.service;

import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Service;

import fds.radar.repository.AccountRepository;

@Service
public class AccountNumberGenerator {
    
    private final AccountRepository accountRepository;

    public AccountNumberGenerator(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    // 랜덤 생성 후 중복일 경우 재시도
    public String generate() {
        String number;
        do {
            number = build();
        } while (accountRepository.existsByAccountNumber(number));
        return number;
    }

    private String build() {
        // 3-4-4-2 형태
        long mid = ThreadLocalRandom.current().nextLong(0, 1_0000_0000L);
        int tail = ThreadLocalRandom.current().nextInt(0, 100);
        return String.format("110-%04d-%04d-%02d",
                            (mid / 10000), (mid % 10000), tail);
    }
}
