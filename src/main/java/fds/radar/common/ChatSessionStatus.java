package fds.radar.common;

import lombok.Getter;

@Getter
public enum ChatSessionStatus {
    WAITING("상담원 연결 대기중"),
    IN_PROGRESS("상담 진행중"),
    CLOSED("상담 종료");

    private final String statusName;
    ChatSessionStatus(String statusName) {
        this.statusName = statusName;
    }

    public String getStatusName() {
        return statusName;
    }
}
