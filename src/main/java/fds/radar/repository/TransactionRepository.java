package fds.radar.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import fds.radar.common.TransactionType;
import fds.radar.entity.transaction.Transactions;

public interface TransactionRepository extends JpaRepository<Transactions, Long>{
    
    // 본인 거래 내역만 조회 (거래 상세)
    Optional<Transactions> findByTransactionIdAndUser_UserId(Long TransactionId, Long userId);

    // 내 거래내역 - 페이징
    Page<Transactions> findByUser_UserId(Long userId, Pageable pageable);

    // 유형, 기간 필터 
    Page<Transactions> findByUser_UserIdAndTransactionTypeAndCreatedAtBetween (
        Long userId, TransactionType type,
        LocalDateTime from, LocalDateTime to, Pageable pageable);

    // 중복 결제 방지 
    Optional<Transactions> findByIdempotencyKey (String idempotencyKey);

    // isNewRecipient 판정용 - 이체이력 확인
    boolean existsByUser_UserIdAndTransactionTypeAndRecipient_AccountNumber(
        Long userId, TransactionType type, String recipientAccountNumber
    );
}
