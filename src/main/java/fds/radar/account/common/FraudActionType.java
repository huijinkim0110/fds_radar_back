package fds.radar.account.common;

import lombok.Getter;

@Getter
public enum FraudActionType {
    HOLD("거래 보류"),
    CONFIRMED("사용자 확인"),
    INVESTIGATE("조사"),
    LOCK("카드/계좌 잠금"),
    FINALIZE("확정");

    private final String typeName;
    FraudActionType(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return typeName;
    }
}
