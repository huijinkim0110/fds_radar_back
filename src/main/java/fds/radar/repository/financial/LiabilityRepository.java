package fds.radar.repository.financial;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import fds.radar.entity.finance.Liabilities;

public interface LiabilityRepository extends JpaRepository<Liabilities, Long> {
    List<Liabilities> findBy_UserId(Long userId);    
}
