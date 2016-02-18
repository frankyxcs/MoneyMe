package com.devmoroz.moneyme.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;


import java.util.UUID;

@DatabaseTable(tableName = "categories")
public class Category implements Parcelable{

    public static final Parcelable.Creator<Category> CREATOR = new Parcelable.Creator<Category>() {
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    @DatabaseField(generatedId = true)
    private UUID id;

    @DatabaseField(canBeNull = false)
    private int color;

    @DatabaseField(canBeNull = false)
    private String title;

    @DatabaseField(canBeNull = false)
    private boolean custom;

    @DatabaseField
    private int order;

    public Category(Parcel parcel) {
        setId(parcel.readString());
        setTitle(parcel.readString());
        setColor(parcel.readInt());
        setCustom(parcel.readInt() != 0);
        setOrder(parcel.readInt());
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(getId());
        parcel.writeString(title);
        parcel.writeInt(color);
        parcel.writeInt(custom ? 1 : 0);
        parcel.writeInt(order);
    }

    public Category() {
        this.custom = false;
    }

    public Category(int color, String title, boolean custom, int order) {
        this.color = color;
        this.title = title;
        this.custom = custom;
        this.order = order;
    }

    public Category(int color, String title, int order) {
        this.color = color;
        this.title = title;
        this.custom = false;
        this.order = order;
    }

    public String getId() {
        return id.toString();
    }

    public void setId(String id) {
        this.id = UUID.fromString(id);
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isCustom() {
        return custom;
    }

    public void setCustom(boolean custom) {
        this.custom = custom;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
