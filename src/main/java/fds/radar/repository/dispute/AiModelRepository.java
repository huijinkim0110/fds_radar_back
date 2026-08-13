package fds.radar.repository.dispute;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import fds.radar.common.ModelType;
import fds.radar.entity.dispute.AiModels;

public interface AiModelRepository extends JpaRepository<AiModels, Long> {
    Optional<AiModels> findFirstByModelTypeAndActiveTrueOrderByRegisteredAtDesc(ModelType modelType);
}