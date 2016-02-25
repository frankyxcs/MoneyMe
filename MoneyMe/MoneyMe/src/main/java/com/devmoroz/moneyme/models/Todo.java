package com.devmoroz.moneyme.models;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;
import java.util.UUID;

@DatabaseTable(tableName = "todo")
public class Todo implements Parcelable {

    public static final Parcelable.Creator<Todo> CREATOR = new Parcelable.Creator<Todo>() {
        public Todo createFromParcel(Parcel in) {
            return new Todo(in);
        }
        public Todo[] newArray(int size) {
            return new Todo[size];
        }
    };

    @DatabaseField(generatedId = true)
    private UUID id;

    @DatabaseField(canBeNull = false)
    private String title;

    @DatabaseField(canBeNull = true)
    private String content;

    @DatabaseField
    private int color;

    @DatabaseField(dataType = DataType.DATE_LONG)
    private Date date;

    @DatabaseField
    private boolean hasReminder;

    public Todo() {
    }

    public Todo(String title, String content, Date date, boolean hasReminder) {
        this.title = title;
        this.content = content;
        this.date = date;
        this.hasReminder = hasReminder;
        this.color = 0;
    }

    public Todo(String title, String content, int color, Date date, boolean hasReminder) {
        this.title = title;
        this.content = content;
        this.color = color;
        this.date = date;
        this.hasReminder = hasReminder;
    }

    public Todo(Parcel parcel) {
        setId(parcel.readString());
        setTitle(parcel.readString());
        setContent(parcel.readString());
        setColor(parcel.readInt());
        setDate((Date) parcel.readSerializable());
        setHasReminder(parcel.readInt() != 0);
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(getId());
        parcel.writeString(title);
        parcel.writeString(content);
        parcel.writeInt(color);
        parcel.writeSerializable(date);
        parcel.writeInt(hasReminder ? 1: 0);
    }

    public String getId() {
        return id.toString();
    }

    public void setId(String id) {
        this.id = UUID.fromString(id);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public Date getDate() {
        return date;
    }

    public Long getDateLong() {
        return date.getTime();
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isHasReminder() {
        return hasReminder;
    }

    public void setHasReminder(boolean hasReminder) {
        this.hasReminder = hasReminder;
    }
}
