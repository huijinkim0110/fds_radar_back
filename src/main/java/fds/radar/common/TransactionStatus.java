package fds.radar.common;

public enum TransactionStatus {
    
    APPROVED("승인"),
    PENDING("보류"),
    CANCELED("취소");

    private final String description;

    TransactionStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
