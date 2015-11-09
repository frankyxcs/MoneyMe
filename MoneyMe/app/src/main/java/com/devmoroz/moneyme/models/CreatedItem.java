package com.devmoroz.moneyme.models;

public class CreatedItem {

    int itemId;
    String category;
    double amount;

    public CreatedItem(int id, String category, double amount) {
        this.itemId = id;
        this.category = category;
        this.amount = amount;
    }

    public int getItemId() {
        return itemId;
    }

    public String getCategory() {
        return category;
    }

    public double getAmount() {
        return amount;
    }
}
