package com.devmoroz.moneyme.models;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;

import com.devmoroz.moneyme.utils.FormatUtils;
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

    @DatabaseField()
    private int alarm_id;

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

    @DatabaseField
    private boolean checkList;

    public Todo() {
        this.alarm_id = (int) System.currentTimeMillis();
    }

    public Todo(String title, String content, Date date, boolean hasReminder, boolean checkList) {
        this.title = title;
        this.content = content;
        this.date = date;
        this.hasReminder = hasReminder;
        this.checkList = checkList;
        this.color = 0;
        this.alarm_id = (int) System.currentTimeMillis();
    }

    public Todo(String title, String content, int color, Date date, boolean hasReminder, boolean checkList) {
        this.title = title;
        this.content = content;
        this.color = color;
        this.date = date;
        this.hasReminder = hasReminder;
        this.checkList = checkList;
        this.alarm_id = (int) System.currentTimeMillis();
    }

    public Todo(Parcel parcel) {
        setId(parcel.readString());
        setAlarm_id(parcel.readInt());
        setTitle(parcel.readString());
        setContent(parcel.readString());
        setColor(parcel.readInt());
        setDate(new Date(parcel.readLong()));
        setHasReminder(parcel.readInt() != 0);
        setCheckList(parcel.readInt() != 0);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(getId());
        parcel.writeInt(getAlarm_id());
        parcel.writeString(title);
        parcel.writeString(content);
        parcel.writeInt(color);
        parcel.writeLong(date.getTime());
        parcel.writeInt(hasReminder ? 1 : 0);
        parcel.writeInt(checkList ? 1 : 0);
    }

    public int getAlarm_id() {
        return alarm_id;
    }

    public void setAlarm_id(int alarm_id) {
        this.alarm_id = alarm_id;
    }

    public String getId() {
        return id != null ? id.toString() : "";
    }

    public void setId(String id) {
        if (FormatUtils.isNotEmpty(id)) {
            this.id = UUID.fromString(id);
        }
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

    public boolean isCheckList() {
        return checkList;
    }

    public void setCheckList(boolean checkList) {
        this.checkList = checkList;
    }
}
