package com.devmoroz.moneyme.models;

import android.os.Parcel;
import android.os.Parcelable;

public class CreatedItem implements Parcelable{

    public static final Parcelable.Creator<CreatedItem> CREATOR = new Parcelable.Creator<CreatedItem>() {
        public CreatedItem createFromParcel(Parcel in) {
            return new CreatedItem(in);
        }

        public CreatedItem[] newArray(int size) {
            return new CreatedItem[size];
        }
    };

    String itemId;
    String category;
    double amount;
    String accountId;

    private CreatedItem(Parcel in) {
        itemId = in.readString();
        category = in.readString();
        amount = (double) in.readValue(Double.class.getClassLoader());
        accountId = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(itemId);
        dest.writeString(category);
        dest.writeValue(amount);
        dest.writeString(accountId);
    }

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

    public boolean isEmpty(){
        return itemId.isEmpty();
    }
}
