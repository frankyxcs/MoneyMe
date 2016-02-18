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

    public Tag(Parcel parcel) {
        setId(parcel.readString());
        setTitle(parcel.readString());
    }

    @Override public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(getId());
        parcel.writeString(getTitle());
    }

    @Override public int describeContents() {
        return 0;
    }

    public Tag() {
    }

    public Tag(String title) {
        this.title = title;
    }

    public Tag(UUID id, String title) {
        this.id = id;
        this.title = title;
    }

    public String getId() {
        return id.toString();
    }

    public String getTitle() {
        return title;
    }

    public void setId(String id) {
        this.id = UUID.fromString(id);
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
