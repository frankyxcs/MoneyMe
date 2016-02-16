package com.devmoroz.moneyme.widgets.homescreen;


public enum WidgetItemType {
    TODO(WidgetItemType.VALUE_TODO), SHOPPING_LIST(WidgetItemType.VALUE_SHOPPING), SCHEDULED_TRANSACTION(WidgetItemType.VALUE_SCHEDULED);

    private static final int VALUE_TODO= 1;
    private static final int VALUE_SHOPPING = 2;
    private static final int VALUE_SCHEDULED = 3;
    private final int value;

    private WidgetItemType(int value) {
        this.value = value;
    }

    public static WidgetItemType fromInt(int value) {
        switch (value) {
            case VALUE_TODO:
                return TODO;
            case VALUE_SHOPPING:
                return SHOPPING_LIST;
            case VALUE_SCHEDULED:
                return SCHEDULED_TRANSACTION;
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
