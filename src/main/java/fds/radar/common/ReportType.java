package fds.radar.common;

import lombok.Getter;

@Getter
public enum ReportType {
    DAMAGE_REPORT("피해 신고"),
    POST_REPORT("사후 신고");

    private final String typeName;
    ReportType(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return typeName;
    }
}
