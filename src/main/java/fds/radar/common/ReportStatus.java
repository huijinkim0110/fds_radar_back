package fds.radar.common;

import lombok.Getter;

@Getter
public enum ReportStatus {
    RECEIVED("접수"),
    UNDER_REVIEW("검토중"),
    PROCESSED("처리 완료");

    private final String statusName;
    ReportStatus(String statusName) {
        this.statusName = statusName;
    }

    public String getStatusName() {
        return statusName;
    }
}
