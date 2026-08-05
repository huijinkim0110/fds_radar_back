package fds.radar.common;

import lombok.Getter;

@Getter
public enum GoalStatus {
    IN_PROGRESS("진행중"),
    ACHIEVED("목표 달성 완료"),
    FAILED("기한 내 미달성"),
    CANCELLED("사용자가 중도 포기/삭제");

    private final String statusName;
    GoalStatus(String statusName) {
        this.statusName = statusName;
    }

    public String getStatusName() {
        return statusName;
    }
}
