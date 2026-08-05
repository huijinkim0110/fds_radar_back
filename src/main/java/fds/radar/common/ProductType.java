package fds.radar.common;

import lombok.Getter;

@Getter
public enum ProductType {
    DEPOSIT("예금"),
    SAVINGS("적금"),
    FUND("펀드"),
    BOND("채권"),
    INSURANCE("저축성 보험"),
    OTHER_PRODUCT("기타");

    private final String typeName;
    ProductType(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return typeName;
    }
}
