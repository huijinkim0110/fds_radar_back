package fds.radar.common;

public enum RiskStatus {
    NORMAL("정상"),
    CAUTION("주의"),
    SUSPICIOUS("의심");

     private final String description;

    RiskStatus(String description) {
        this.description = description;
    }
 
    public String getDescription() {
        return description;
    }
}
