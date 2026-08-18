package fds.radar.dto.fraud;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * FastAPI 예측 서버(/predict)로 보내는 요청 형식.
 * FastAPI 쪽 schemas.py의 TransactionRequest와 필드명이 정확히 일치해야 함
 * (JSON 직렬화 시 필드명 그대로 매핑되기 때문).
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiPredictRequest {
    private String transactionType;
    private BigDecimal amount;
    private String occurredAt;
    private String transactionChannel;
    private String countryCode;
    private String merchantName;
    private boolean newRecipient;
    private String merchantCategory;
}