package fds.radar.service.fraud.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 거래 승인/취소 처리 결과만 담는 내부 전용 객체.
 * LockResult와 동일한 역할 — TransactionStatusService가 반환한 순수 결과를
 * FraudCaseService가 받아서 FraudCases/Transactions 갱신 + 이력 기록에 씀.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionStatusResult {
    private boolean success;
    private String message;
}