package fds.radar.account.common;

public enum CardType {
    CREDIT("신용카드"),
    CHECK("체크카드");

    private final String description;

    CardType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
