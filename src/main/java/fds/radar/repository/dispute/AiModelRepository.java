package fds.radar.repository.dispute;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import fds.radar.common.ModelType;
import fds.radar.entity.dispute.AiModels;

public interface AiModelRepository extends JpaRepository<AiModels, Long> {
    Optional<AiModels> findFirstByModelTypeAndActiveTrueOrderByRegisteredAtDesc(ModelType modelType);

    // 카드결제(AutoML)/계좌이체(HistGradientBoosting)가 서로 다른 모델을 쓰는데
    // 기존 메서드는 거래타입 구분 없이 최신 active 모델 하나만 가져와서 결과에 잘못된 모델이 매칭되는 문제가 있었음
    // → algorithm까지 같이 조회해서 거래타입에 맞는 모델을 정확히 찾도록 추가
    Optional<AiModels> findFirstByModelTypeAndAlgorithmAndActiveTrueOrderByRegisteredAtDesc(
        ModelType modelType, String algorithm);
}
