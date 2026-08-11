package fds.radar.repository.account;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.query.Param;

import fds.radar.entity.account.Cards;
import jakarta.persistence.LockModeType;

public interface CardRepository extends JpaRepository<Cards, Long> {
    
    // 내 카드 목록
    List<Cards> findByUserId(Long userId);

    // 본인 소유 검증 - 타인 카드 id 조회 차단용
    Optional<Cards> findByCardIdAndUserId(Long cardId, Long userId);

    // 특정 계좌에 딸린 카드들 (계좌 해지 시 카드 정리 확인
    List<Cards> findByAccountId(Long accountId);

    // 발급 번호 중복 체크 / 카드번호 조회
    Optional<Cards> findByCardNumber(String cardNumber);
    boolean existsByCardNumber(String cardNumber);

    // 결제 시 사용가능한도 차감할 때 쓰는 비관적 락 조회 
    // 트랜잭션 안에서만 호출! 
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Cards c where c.cardId = :id")
    Optional<Cards> findByCardIdForUpdate(@Param("id") Long id);
}
