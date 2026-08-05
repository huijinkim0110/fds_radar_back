package fds.radar.common;

public enum LiabilityType {
    CREADIT_LOAN("신용 대출"),
    MORTGAGE("주택 담보 대출"),
    AUTO_LOAN("자동차 대출"),
    STUDENT_LOAN("학자금 대출"),
    CARD_LOAN("카드론"),
    OTHER_LIABILITY("기타 부채");

    private final String description;

    LiabilityType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
