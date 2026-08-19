package fds.radar.repository.financial;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import fds.radar.entity.finance.FinancialProfiles;

public interface FinancialProfileRepository extends JpaRepository<FinancialProfiles, Long> {
    static Optional<FinancialProfiles> findByUser_UserId(Long userId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByUser_UserId'");
    }
}
