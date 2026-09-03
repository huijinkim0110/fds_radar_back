package fds.radar.repository.financialProduct;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import fds.radar.common.SubscriptionStatus;
import fds.radar.entity.financialProduct.SimulatedSubscriptions;

public interface SimulatedSubscriptionsRepository extends JpaRepository<SimulatedSubscriptions, Long> {
    // 사용자가 가입한 상품 중 특정 상태(ACTIVE/CANCELLED/COMPLETED)인 것만 조회
    List<SimulatedSubscriptions> findByUser_UserIdAndSubscriptionStatus(Long userId, SubscriptionStatus status);

    // 스케줄러용 - 특정 상태 전체 조회(월 납입/만기 처리 대상 훑기)
    List<SimulatedSubscriptions> findBySubscriptionStatus(SubscriptionStatus status);
}
