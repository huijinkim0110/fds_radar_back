package fds.radar.common;

public enum RiskTendency {
    STABLE("안정형(원금 손실 최소화 우선"),
    NEUTRAL("중립형"),
    ACTIVE("적극형"),
    AGGRESSIVE("공격형(고위험 · 고수익 추구)");

    private final String statusName;

    RiskTendency(String statusName) {
        this.statusName = statusName;
    }

    public String getStatusName() {
        return statusName;
    }
}
