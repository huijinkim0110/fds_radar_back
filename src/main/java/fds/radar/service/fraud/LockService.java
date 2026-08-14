package fds.radar.service.fraud;

import fds.radar.common.RequestTargetType;
import fds.radar.service.fraud.vo.LockResult;

/**
 * 카드/계좌 잠금 처리를 담당하는 인터페이스.
 *
 * FraudModelService와 동일한 이유로 인터페이스로 분리:
 *   - 지금은 MockLockService(무조건 성공)를 쓰지만,
 *     나중에 C가 실제 카드/계좌 잠금 처리 로직을 완성하면
 *     @Service 구현체만 교체하면 됨. FraudCaseService는 이 인터페이스만
 *     바라보고 있어서 교체 시 영향 없음.
 */
public interface LockService {

    /**
     * @param targetType 잠글 대상 (CARD / ACCOUNT)
     * @param userId     잠금 대상 사용자 (그 사건의 당사자)
     * @return 처리 결과 (성공 여부 + 메시지)
     */
    LockResult lock(RequestTargetType targetType, Long userId);
}