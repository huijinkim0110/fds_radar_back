package fds.radar.common;

import lombok.Getter;

@Getter
public enum UserStatus {
    ACTIVE("정상 이용중"),
    SUSPENDED("이상거래 의심 등으로 계정 정지"),
    LOCKED("로그인 실패 누적 등으로 계정 잠금"),
    WITHDRAWN("회원 탈퇴");

    private final String statusName;
    UserStatus(String statusName) {
        this.statusName = statusName;
    }

    public String getStatusName() {
        return statusName;
    }
}
