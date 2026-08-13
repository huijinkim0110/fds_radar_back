package fds.radar.repository.financial;

import org.springframework.data.jpa.repository.JpaRepository;

import fds.radar.entity.account.Institutions;

public interface FinancialInstitutionRepository extends JpaRepository<Institutions, Long> {
    
}
