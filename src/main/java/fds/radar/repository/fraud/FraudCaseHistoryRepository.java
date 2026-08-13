package fds.radar.repository.fraud;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import fds.radar.entity.fraud.FraudCaseHistories;

public interface FraudCaseHistoryRepository extends JpaRepository<FraudCaseHistories, Long> {

    List<FraudCaseHistories> findByFraudCase_FraudCaseIdOrderByCreatedAtAsc(Long fraudCaseId);
}