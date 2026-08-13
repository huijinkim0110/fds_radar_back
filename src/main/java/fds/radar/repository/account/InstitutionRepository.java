package fds.radar.repository.account;

import fds.radar.entity.account.Institutions;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstitutionRepository extends JpaRepository<Institutions, Long> {
}