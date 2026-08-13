package fds.radar.repository.account;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import fds.radar.entity.account.Accounts;
import jakarta.persistence.LockModeType;

public interface AccountRepository extends JpaRepository<Accounts, Long> {
    
    // 내 계좌 목록
    List<Accounts> findByUser_UserId(Long userId);

    // 본인 소유 계좌 검증 - 타인 계좌 id 조회 차단용
    Optional<Accounts> findByAccountIdAndUser_UserId(Long accountId, Long userId);

    // 발급 번호 중복 체크 / 계좌번호 조회
    Optional<Accounts> findByAccountNumber(String accountNumber);
    boolean existsByAccountNumber(String accountNumber);

    // 이체 로직에서 잔액 차감할 때 쓰는 락 조회
    // 트랜잭션 안에서만 호출해야 락 유지
    // 중복된 계좌번호로 인한 잔액 차감 오류 방지
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Accounts a WHERE a.accountId = :id")
    Optional<Accounts> findByAccountIdForUpdate(@Param("id") Long id);

}
