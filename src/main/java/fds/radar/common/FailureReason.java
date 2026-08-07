package fds.radar.common;

import lombok.Getter;

@Getter
public enum FailureReason {
    WRONG_PASSWORD("비밀번호 불일치"),
    USER_NOT_FOUND("존재하지 않는 사용자"),
    ACCOUNT_LOCKED("계정 잠김"),
    ACCOUNT_SUSPENDED("계정 정지"),
    ACCOUNT_WITHDRAWN("탈퇴한 계정");

    private final String description;

    FailureReason(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
