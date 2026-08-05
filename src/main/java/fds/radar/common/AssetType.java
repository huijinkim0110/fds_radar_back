package fds.radar.common;

import lombok.Getter;

@Getter
public enum AssetType {
    CASH("현금"),
    DEPOSIT("예금"),
    SAVINGS("적금"),
    STOCK("주식"),
    FUND("펀드"),
    REAL_ESTATE("부동산"),
    OTHER_ASSET("기타 자산");

    private final String typeName;
    AssetType(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return typeName;
    }
}
