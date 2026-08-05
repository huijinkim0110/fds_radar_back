package fds.radar.common;

import lombok.Getter;

@Getter
public enum LoginResult {
    SUCCESS("성공"),
    FAIL("실패");

    private final String description;

    LoginResult(String description) {
        this.description = description;
    }
}
