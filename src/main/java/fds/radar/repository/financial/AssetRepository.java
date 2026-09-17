package fds.radar.repository.financial;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import fds.radar.entity.finance.Assets;

public interface AssetRepository extends JpaRepository<Assets, Long> {
    List<Assets> findByUser_UserId(Long userId);

    // 본인 소유 자산 검증용
    Optional<Assets> findByAssetIdAndUser_UserId(Long assetId, Long userId);
}
