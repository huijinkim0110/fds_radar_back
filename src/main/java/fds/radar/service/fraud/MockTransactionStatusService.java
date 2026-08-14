package fds.radar.service.fraud;

import org.springframework.stereotype.Service;

import fds.radar.common.TransactionStatus;
import fds.radar.entity.transaction.Transactions;
import fds.radar.repository.transaction.TransactionRepository;
import fds.radar.service.fraud.vo.TransactionStatusResult;
import lombok.RequiredArgsConstructor;

/**
 * 거래 상태 변경을 실제 정합성 로직(잔액 롤백, 한도 복구 등) 없이
 * 상태값만 그대로 반영하는 Mock 구현체.
 *
 * C가 실제 로직(잔액/한도 처리 포함)을 완성하기 전까지
 * "최종판정 → 거래상태 변경 → 사건종결" 전체 플로우를 먼저 검증하기 위한 용도.
 *
 * MockLockService와 달리 이건 실제로 DB에 상태값을 반영해요 — 이유는 최종판정
 * API 호출 후 바로 거래 상세를 조회했을 때 상태가 안 바뀌어 있으면 프론트 쪽 확인이
 * 안 되기 때문. "성공했다고 치는" 수준이 아니라 "최소한의 진짜 반영"까지는 함.
 */
@Service
@RequiredArgsConstructor
public class MockTransactionStatusService implements TransactionStatusService {

    private final TransactionRepository transactionRepository;

    @Override
    public TransactionStatusResult updateStatus(Long transactionId, TransactionStatus newStatus) {
        Transactions transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 거래입니다. id=" + transactionId));

        transaction.setTransactionStatus(newStatus);
        transactionRepository.save(transaction);

        return TransactionStatusResult.builder()
                .success(true)
                .message("거래 상태 변경 완료 (Mock): " + newStatus)
                .build();
    }
}