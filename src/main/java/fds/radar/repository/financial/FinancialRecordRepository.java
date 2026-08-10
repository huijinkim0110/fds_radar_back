package fds.radar.repository.financial;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import fds.radar.entity.finance.FinancialRecords;

public interface FinancialRecordRepository extends JpaRepository<FinancialRecords, Long> {
    List<FinancialRecords> findByUserId(Long userId);
    List<FinancialRecords> findByUserIdAndCategory(Long userId, Long Category);
}
