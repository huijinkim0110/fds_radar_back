package fds.radar.repository.transaction;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import fds.radar.entity.transaction.Merchants;
import fds.radar.common.RiskStatus;


public interface MerchanstRepository extends JpaRepository<Merchants, Long>{

    // 등록 시 중복 확인
    boolean existsByMerchantName(String merchantName);
    Optional<Merchants> findByMerchantName(String name);

    // 위험 상태 별 조회 (의심, 차단 가맹점 관리, 모니터링용)
    List<Merchants> findByRiskStatus(RiskStatus riskStatus);
    
}
