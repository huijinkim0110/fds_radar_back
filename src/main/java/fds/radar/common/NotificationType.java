package fds.radar.common;

import lombok.Getter;

@Getter
public enum NotificationType {
    FINANCIAL_PRODUCT_RECOMMENDATION("금융 상품 추천"),
    FRAUD_ALERT("이상거래 확인 요청"),
    TRANSACTION_APPROVAL("거래 승인/보류 알림"),
    ACCOUNT_LOCK("카드/계좌 잠금 알림"),
    REPORT_RESULT("신고 및 이의제기 결과 알림");

    private final String typeName;
    NotificationType(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return typeName;
    }
}
