package fds.radar.repository.financial;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import fds.radar.entity.finance.FinancialProfiles;

public interface FinancialProfileRepository extends JpaRepository<FinancialProfiles, Long> {
    Optional<FinancialProfiles> findByUserId(Long useeId);
}
