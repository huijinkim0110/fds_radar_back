package fds.radar.repository.financial;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import fds.radar.entity.finance.Assets;

public interface AssetRepository extends JpaRepository<Assets, Long> {
    List<Assets> findByUser_UserId(Long userId);
}
