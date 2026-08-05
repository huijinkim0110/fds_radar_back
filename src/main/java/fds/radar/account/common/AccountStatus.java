package fds.radar.account.common;

public enum AccountStatus {
    ACTIVE("활동 중"),
    ACCOUNT_BLOCKED("계좌 정지"),
    CLOSED("해지");

    private final String description;

    AccountStatus(String description) {
        this.description = description;
    }
 
    public String getDescription() {
        return description;
    }

}
