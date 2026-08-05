package fds.radar.common;

public enum IncomeSource {
    EARNED_INCOME("근로소득(급여)"),
    BUSINESS_INCOME("사업소득(자영업 등)"),
    PENSION_INCOME("연금소득"),
    INVESTMENT_INCOME("이자 · 배당 · 투자소득"),
    OTHERM("임대소득 등"),
    NONE("없음");

    private final String description;

    IncomeSource(String description) {
        this.description = description;
    }
 
    public String getDescription() {
        return description;
    }
}
