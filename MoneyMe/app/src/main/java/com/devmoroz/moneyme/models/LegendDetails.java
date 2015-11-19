package com.devmoroz.moneyme.models;

/**
 * Created by Vitalii_Moroz on 11/19/2015.
 */
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
