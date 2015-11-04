package com.devmoroz.moneyme.models;

public class ChartVM {
    String category;
    float amount;

    public void setCategory(String category) {
        this.category = category;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getCategory() {

        return category;
    }

    public float getAmount() {
        return amount;
    }

    public ChartVM(String category, float amount) {

        this.category = category;
        this.amount = amount;
    }
}
