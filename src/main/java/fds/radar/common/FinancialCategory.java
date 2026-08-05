package fds.radar.common;

import lombok.Getter;

@Getter
public enum FinancialCategory {
    // INCOME
    SALARY,
    BUSINESS,
    INVESTMENT,
    PENSION,
    OTHER_INCOME,

    // EXPENSE
    FOOD,
    TRANSPORTATION,
    HOUSING,
    SHOPPING,
    MEDICAL,
    LEISURE,
    EDUCATION,
    OTHER_EXPENSE
}
