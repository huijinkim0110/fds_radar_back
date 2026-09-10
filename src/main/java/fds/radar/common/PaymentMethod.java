package fds.radar.common;

import lombok.Getter;

@Getter 
public enum PaymentMethod {
    LUMP_SUM("일시납"),
    INSTALLMENT("월납"),
    MIXED("혼합(일시납+월납)");

    private final String methodName;
    PaymentMethod(String methodName) {
        this.methodName = methodName;
    }
}
