package fds.radar.repository.transaction;

import org.springframework.data.jpa.repository.JpaRepository;

import fds.radar.entity.transaction.Transactions;

public interface TransactionRepository extends JpaRepository<Transactions, Long>{
    
    // TODO: 필드명 불일치로 임시 주석 처리 (5차 이후 C한테 수정 요청)
    // Transactions 엔티티 PK는 "transactionId"이지 "id"가 아니고,
    // 유저 필드명도 "users"라서 "findByIdAndUserId"가 매칭이 안 됨
    // → 고치려면: findByTransactionIdAndUsers_UserId(Long transactionId, Long userId)
    // Optional<Transactions> findByIdAndUserId(Long id, Long userId);

    // TODO: 임시 주석 처리
    // "userId" 자체는 users.userId로 중첩 접근 가능해서 살릴 수도 있지만
    // 지금 안 쓰니까 일단 주석. 나중에 필요하면 findByUsers_UserId로 이름 바꿔서 사용
    // Page<Transactions> findByUserId(Long userId, Pageable pageable);

    // TODO: 임시 주석 처리
    // "type" → transactionType, "createdAt" → 엔티티에 그런 필드 자체가 없음(occurredAt만 있음)
    // Page<Transactions> findByUserIdAndTypeAndCreatedAtBetween(
    //     Long userId, TransactionType type,
    //     LocalDateTime from, LocalDateTime to, Pageable pageable);

    // TODO: 임시 주석 처리
    // "idempotencyKey" 필드가 Transactions 엔티티에 없음 (엔티티에 필드 추가 필요)
    // Optional<Transactions> findByIddemotencyKey(String idempotencyKey);

    // TODO: 임시 주석 처리
    // "recipientAccountNumber" 필드가 없고, recipient(TransferRecipients) 연관관계를 타야 함
    // → 고치려면: existsByUsers_UserIdAndTransactionTypeAndRecipient_AccountNumber(...)
    // boolean existsByUserIdAndTypeAndRecipientAccountNumber(
    //     Long userId, TransactionType type, String recipientAccountNumber
    // );
}