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

    @DatabaseField(dataType = DataType.DATE_LONG)
    private Date createdDate;

    @DatabaseField(canBeNull = false)
    private String title;

    @DatabaseField(canBeNull = true)
    private String content;

    @DatabaseField
    private int color;

    @DatabaseField(dataType = DataType.DATE_LONG)
    private Date alarmDate;

    @DatabaseField(dataType = DataType.DATE_LONG)
    private Date updatedDate;

    @DatabaseField
    private boolean hasReminder;

    @DatabaseField
    private boolean checkList;

    public Todo() {
    }

    public Todo(String title, String content, Date createdDate, boolean hasReminder, boolean checkList) {
        this.title = title;
        this.content = content;
        this.createdDate = createdDate;
        this.updatedDate = createdDate;
        this.hasReminder = hasReminder;
        this.checkList = checkList;
        this.color = 0;
    }

    public Todo(String title, String content, int color, Date createdDate,Date updatedDate, boolean hasReminder, boolean checkList) {
        this.title = title;
        this.content = content;
        this.color = color;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.hasReminder = hasReminder;
        this.checkList = checkList;
    }

    public Todo(Parcel parcel) {
        setId(parcel.readString());
        setTitle(parcel.readString());
        setContent(parcel.readString());
        setColor(parcel.readInt());
        setCreatedDateLong(parcel.readLong());
        setUpdatedDateLong(parcel.readLong());
        setAlarmDateLong(parcel.readLong());
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
        parcel.writeString(title);
        parcel.writeString(content);
        parcel.writeInt(color);
        parcel.writeLong(createdDate != null ? createdDate.getTime() : 0);
        parcel.writeLong(updatedDate != null ? updatedDate.getTime() : 0);
        parcel.writeLong(alarmDate != null ? alarmDate.getTime() : 0);
        parcel.writeInt(hasReminder ? 1 : 0);
        parcel.writeInt(checkList ? 1 : 0);
    }

    public int getAlarm_id() {
        return (int) createdDate.getTime();
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

    public Date getAlarmDate() {
        return alarmDate;
    }

    public void setAlarmDate(Date alarmDate) {
        this.alarmDate = alarmDate;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public void setAlarmDateLong(long alarmDate) {
        if (alarmDate != 0) {
            this.alarmDate = new Date(alarmDate);
        }
    }

    public void setUpdatedDateLong(long updatedDate) {
        if (updatedDate != 0) {
            this.updatedDate = new Date(updatedDate);
        }
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public void setCreatedDateLong(long createdDate) {
        if (createdDate != 0) {
            this.createdDate = new Date(createdDate);
        }
    }

    public long getAlarmDateLong() {
        return hasReminder ? alarmDate.getTime() : 0;
    }

    public long getUpdatedDateLong() {
        return updatedDate != null ? updatedDate.getTime() : 0;
    }

    public boolean isHasReminder() {
        return hasReminder;
    }

    public boolean isShowReminderIcon() {
        return hasReminder && (getAlarmDate() != null && System.currentTimeMillis() < getAlarmDateLong());
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
