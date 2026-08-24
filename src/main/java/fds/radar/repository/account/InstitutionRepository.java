package fds.radar.repository.account;

import fds.radar.entity.account.Institutions;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


public interface InstitutionRepository extends JpaRepository<Institutions, Long> {

    // 금감원 API에서 가져온 은행명으로 기존 기관 찾기
    // - 이미 등록된 은행이면 재사용, 없으면 새로 생성(FssProductSyncService에서 처리)
    Optional<Institutions> findByInstitutionName(String institutionName);
}