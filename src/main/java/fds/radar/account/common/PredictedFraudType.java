package fds.radar.account.common;

import lombok.Getter;

@Getter
public enum PredictedFraudType {
    ACCOUNT_TAKEOVER("계정/카드 도용"),
    UNUSUAL_TRANSFER("이상 송금"),
    STOLEN_CARD("도난/분실 카드 사용 의심"),
    MONEY_LAUNDERING_PATTERN("자금세탁 의심 패턴"),
    OTHER_FRAUD_TYPE("기타");

    private final String fraudType;
    PredictedFraudType(String fraudType) {
        this.fraudType = fraudType;
    }

    public String getFraudType() {
        return fraudType;
    }
}
