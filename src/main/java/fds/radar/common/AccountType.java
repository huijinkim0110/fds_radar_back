package fds.radar.common;

public enum AccountType {
    CHECKING("입출금"),
    DEPOSIT("예금"),
    SAVINGS("적금");

    private final String description;

    AccountType(String description) {
        this.description = description;
    }
 
    public String getDescription() {
        return description;
    }
}
