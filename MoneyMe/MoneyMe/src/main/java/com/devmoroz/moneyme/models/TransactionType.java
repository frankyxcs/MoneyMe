package com.devmoroz.moneyme.models;


public enum TransactionType {
    INCOME(TransactionType.VALUE_INCOME), OUTCOME(TransactionType.VALUE_OUTCOME), TRANSFER(TransactionType.VALUE_TRANSFER);

    private static final int VALUE_INCOME= 1;
    private static final int VALUE_OUTCOME = 2;
    private static final int VALUE_TRANSFER = 3;
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
            case VALUE_TRANSFER:
                return TRANSFER;
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
