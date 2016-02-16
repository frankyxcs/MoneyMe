package com.devmoroz.moneyme.models;

public class CreatedItem {

    String itemId;
    String category;
    double amount;
    String accountId;

    public CreatedItem(String id, String category, double amount, String accountId) {
        this.itemId = id;
        this.category = category;
        this.amount = amount;
        this.accountId = accountId;
    }

    public String getItemId() {
        return itemId;
    }

    public String getCategory() {
        return category;
    }

    public double getAmount() {
        return amount;
    }

    public String getAccountId() {
        return accountId;
    }
}
