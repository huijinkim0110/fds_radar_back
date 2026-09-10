package fds.radar.common;

import lombok.Getter;

@Getter
public enum ChatSenderType {
    BOT("챗봇"),
    USER("사용자"),
    ADMIN("관리자"),
    SYSTEM("시스템");

    private final String senderName;
    ChatSenderType(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderName() {
        return senderName;
    }
}
