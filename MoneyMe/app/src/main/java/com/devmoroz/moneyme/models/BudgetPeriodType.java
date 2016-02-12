package com.devmoroz.moneyme.models;


public enum BudgetPeriodType {
    Week(BudgetPeriodType.VALUE_WEEK),
    Month(BudgetPeriodType.VALUE_MONTH),
    Quarter(BudgetPeriodType.VALUE_QUARTER),
    Year(BudgetPeriodType.VALUE_YEAR);

    private static final int VALUE_WEEK = 1;
    private static final int VALUE_MONTH = 2;
    private static final int VALUE_QUARTER = 3;
    private static final int VALUE_YEAR= 4;

    private final int value;

    private BudgetPeriodType(int value) {
        this.value = value;
    }

    public static BudgetPeriodType fromInt(int value) {
        switch (value) {
            case VALUE_WEEK:
                return Week;
            case VALUE_MONTH:
                return Month;
            case VALUE_QUARTER:
                return Quarter;
            case VALUE_YEAR:
                return Year;
            default:
                throw new IllegalArgumentException("Value " + value + " is not supported.");
        }
    }

    public int asInt() {
        return value;
    }

    public String asString() {
        return String.valueOf(value);
    }
}
