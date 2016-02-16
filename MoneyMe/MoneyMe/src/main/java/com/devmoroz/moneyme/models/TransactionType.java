package com.devmoroz.moneyme.models;


public enum TransactionType {
    INCOME(TransactionType.VALUE_INCOME), OUTCOME(TransactionType.VALUE_OUTCOME);

    private static final int VALUE_INCOME= 1;
    private static final int VALUE_OUTCOME = 2;
    private final int value;

    private TransactionType(int value) {
        this.value = value;
    }

    public static TransactionType fromInt(int value) {
        switch (value) {
            case VALUE_INCOME:
                return INCOME;
            case VALUE_OUTCOME:
                return OUTCOME;
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
