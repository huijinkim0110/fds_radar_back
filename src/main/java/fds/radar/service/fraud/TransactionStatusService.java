package fds.radar.service.fraud;

import fds.radar.common.TransactionStatus;
import fds.radar.service.fraud.vo.TransactionStatusResult;

/**
 * 최종 판정에 따른 거래 상태(승인/취소) 변경을 담당하는 인터페이스.
 *
 * LockService와 동일한 이유로 분리:
 *   - 지금은 MockTransactionStatusService(무조건 성공)를 쓰지만,
 *     C가 실제 거래 상태 변경 로직(잔액 롤백, 한도 복구 등 정합성 처리 포함)을
 *     완성하면 구현체만 교체하면 됨.
 */
public interface TransactionStatusService {

    /**
     * @param transactionId 상태를 바꿀 거래
     * @param newStatus     APPROVED(정상 확정) 또는 CANCELED(사기로 취소)
     * @return 처리 결과 (성공 여부 + 메시지)
     */
    TransactionStatusResult updateStatus(Long transactionId, TransactionStatus newStatus);
}