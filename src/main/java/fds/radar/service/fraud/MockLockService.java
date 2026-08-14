package fds.radar.service.fraud;

import org.springframework.stereotype.Service;

import fds.radar.common.RequestTargetType;
import fds.radar.service.fraud.vo.LockResult;

/**
 * 카드/계좌 잠금을 실제로 처리하지 않고 항상 성공만 반환하는 Mock 구현체.
 *
 * C가 실제 잠금 처리 로직(카드 상태 BLOCKED 변경, 계좌 상태 변경 등)을 완성하기 전까지
 * 이 구현체로 전체 플로우(요청 → 처리 → 이력기록)를 먼저 검증하기 위한 용도.
 *
 * MockFraudModelService와 동일한 역할: "일단 무조건 성공"으로 두고
 * 위 레이어(FraudCaseService, Controller, 이력기록)가 제대로 동작하는지부터 확인.
 */
@Service
public class MockLockService implements LockService {

    @Override
    public LockResult lock(RequestTargetType targetType, Long userId) {
        return LockResult.builder()
                .success(true)
                .message(targetType + " 잠금 처리 완료 (Mock)")
                .build();
    }
}