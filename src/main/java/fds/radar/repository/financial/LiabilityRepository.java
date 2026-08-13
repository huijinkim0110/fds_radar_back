package fds.radar.repository.financial;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import fds.radar.entity.finance.Liabilities;

public interface LiabilityRepository extends JpaRepository<Liabilities, Long> {
    List<Liabilities> findByUser_UserId(Long userId);    
}
