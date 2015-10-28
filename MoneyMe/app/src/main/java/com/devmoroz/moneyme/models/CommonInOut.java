package com.devmoroz.moneyme.models;

public class CommonInOut {

    int type;
    int id;
    String amount;
    String description;

    public CommonInOut(int type, int id, String amount, String description) {
        this.type = type;
        this.id = id;
        this.amount = amount;
        this.description = description;
    }

    public int getType() {
        return type;
    }

    public int getId() {
        return id;
    }

    public String getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }
}
