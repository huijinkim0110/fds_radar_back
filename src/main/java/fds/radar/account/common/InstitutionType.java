package fds.radar. account.common;

public enum InstitutionType {
    BANK("은행"),
    CARD_COMPANY("카드사"),
    SECURITIES("증권사"),
    INSURANCE("보험사"),
    OTHER_INSTITURION("기타");

    private final String description;

    InstitutionType(String description) {
        this.description = description;
    }
 
    public String getDescription() {
        return description;
    }
}
