package com.devmoroz.moneyme.models;


import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.UUID;

@DatabaseTable(tableName = "tags")
public class Tag implements Parcelable{

    public static final Parcelable.Creator<Tag> CREATOR = new Parcelable.Creator<Tag>() {
        public Tag createFromParcel(Parcel in) {
            return new Tag(in);
        }

        public Tag[] newArray(int size) {
            return new Tag[size];
        }
    };

    @DatabaseField(generatedId = true)
    UUID id;

    @DatabaseField
    String title;

    @DatabaseField
    String categoryId;

    public Tag(Parcel parcel) {
        setId(parcel.readString());
        setTitle(parcel.readString());
        setCategoryId(parcel.readString());
    }

    @Override public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(getId());
        parcel.writeString(getTitle());
        parcel.writeString(getCategoryId());
    }

    @Override public int describeContents() {
        return 0;
    }

    public Tag() {
    }

    public Tag(String title, String categoryId) {
        this.title = title;
        this.categoryId = categoryId;
    }

    public Tag(UUID id, String title, String categoryId) {
        this.id = id;
        this.title = title;
        this.categoryId = categoryId;
    }

    public String getId() {
        return id.toString();
    }

    public String getTitle() {
        return title;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setId(String id) {
        this.id = UUID.fromString(id);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }
}
