package fds.radar.common;

public enum InstitutionType {
    BANK("은행"),
    CARD_COMPANY("카드사"),
    SECURITIES("증권사"),
    INSURANCE("보험사"),
    ASSET_MANAGEMENT("자산운용사"),
    OTHER_INSTITUTION("기타");

    private final String description;

    InstitutionType(String description) {
        this.description = description;
    }
 
    public String getDescription() {
        return description;
    }
}
