package fds.radar.service.fraud.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import fds.radar.common.BusinessCategory;
import fds.radar.common.TransactionChannel;
import fds.radar.common.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * AI 모델 입력값 변환 전 단계의 거래 정보 스냅샷.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionData {
    private Long transactionId;
    private BigDecimal amount;
    private LocalDateTime occurredAt;
    private TransactionType transactionType;
    private TransactionChannel transactionChannel;
    private String countryCode;
    private String merchantName;
    private boolean newRecipient;
    private BusinessCategory merchantCategory; // 추가: 가맹점 업종 (카드 모델 2번째로 중요한 피처)
}