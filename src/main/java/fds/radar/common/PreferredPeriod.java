package fds.radar.common;

public enum PreferredPeriod {
    SHORT_TERM("단기(1년 미만)"),
    MID_TERM("중기(1~3년)"),
    LONG_TERM("장기(3년 이상)");

    
    private final String statusName;

    PreferredPeriod(String statusName) {
        this.statusName = statusName;
    }

    public String getStatusName() {
        return statusName;
    }
}
