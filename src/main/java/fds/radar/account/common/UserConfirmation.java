package fds.radar.account.common;

import lombok.Getter;

@Getter
public enum UserConfirmation {
    CONFIRMED("본인"),
    DENIED("아님"),
    NO_RESPONSE("미응답");

    private final String confirmationResult;
    UserConfirmation(String confirmationResult) {
        this.confirmationResult = confirmationResult;
    }

    public String getConfirmationResult() {
        return confirmationResult;
    }
}
