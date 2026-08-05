package fds.radar.common;

public enum LossTolerance {
    NONE("손실울 전혀 감내할 수 없음"),
    LOW("소폭의 손실만 감내 가능"),
    MEDIUM("어느 정도 손실 감내 가능"),
    HIGH("높은 손실도 감당 가능");

    
    private final String statusName;

    LossTolerance(String statusName) {
        this.statusName = statusName;
    }

    public String getStatusName() {
        return statusName;
    }
}
