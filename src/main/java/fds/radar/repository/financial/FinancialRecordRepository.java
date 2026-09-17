package fds.radar.repository.financial;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import fds.radar.common.FinancialCategory;
import fds.radar.entity.finance.FinancialRecords;

public interface FinancialRecordRepository extends JpaRepository<FinancialRecords, Long> {
    List<FinancialRecords> findByUser_UserId(Long userId);
    List<FinancialRecords> findByUser_UserIdAndFinancialCategory(Long userId, FinancialCategory financialCategory);

    // 본인 소유 기록 검증용
    Optional<FinancialRecords> findByRecordIdAndUser_UserId(Long recordId, Long userId);

}
