package com.devmoroz.moneyme.models;


public class LegendDetails {
    public int ColorCode;
    public String CategoryName;
    public float Amount;
    public float AmountPercent;

    public LegendDetails(int colorCode, String categoryName, float amount, float amountPercent) {
        ColorCode = colorCode;
        CategoryName = categoryName;
        Amount = amount;
        AmountPercent = amountPercent;
    }
}
