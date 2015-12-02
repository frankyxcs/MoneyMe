package com.devmoroz.moneyme.models;

public class CreatedItem {

    int itemId;
    String category;
    double amount;
    int accountId;

    public CreatedItem(int id, String category, double amount, int accountId) {
        this.itemId = id;
        this.category = category;
        this.amount = amount;
        this.accountId = accountId;
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

    public int getAccountId() {
        return accountId;
    }
}
