package fds.radar.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import fds.radar.entity.account.TransferRecipients;

public interface TransferRepository extends JpaRepository<TransferRecipients, Long>{

   // 내 수취인 목록
   List<TransferRecipients> findByIdAndUserId(Long userId);

   // 본인 소유 검증 - 타인 수취인 삭제 차단
   Optional<TransferRecipients> findByIdAndUserId(Long id, Long userId);
    
   // 저장 시 중복 방시 - 가틍ㄴ 사람이 같은 은행 + 계좌 중복 저장 안되게
   boolean existsByUserIdAndBankCodeAndAccountNumber (
        Long userId, String bankCode, String accountNumber
   );
}
