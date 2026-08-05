package fds.radar.account.common;

public enum InstitutionStatus {
    ACTIVE("활성"),
    INACTIVE("비활성");

    private final String description;

    InstitutionStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
        
    }

}
