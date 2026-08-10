package fds.radar.repository.fraud;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import fds.radar.entity.fraud.FraudDetectionResults;

public interface FraudDetectionResultRepository extends JpaRepository<FraudDetectionResults, Long> {
    List<FraudDetectionResults> findByTransaction_TransactionId(Long transactionId);

    Optional<FraudDetectionResults> findTopByTransaction_TransactionIdOrderByDetectedAtDesc(Long transactionId);
}
