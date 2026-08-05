package fds.radar.common;

public enum CardStatus {
    ACTIVE("활성"),
    SUSPENDED("일시정지"),
    LOCKED("잠금"),
    EXPIRED("만료");

    private final String description;

    CardStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
