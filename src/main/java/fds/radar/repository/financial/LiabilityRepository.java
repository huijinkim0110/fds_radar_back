package fds.radar.repository.financial;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import fds.radar.entity.finance.Liabilities;

public interface LiabilityRepository extends JpaRepository<Liabilities, Long> {
    List<Liabilities> findByUser_UserId(Long userId); 
    
    // 본인 소유 부채 검증용
    Optional<Liabilities> findByLiabilityIdAndUser_UserId(Long liabilityId, Long userId);
}
