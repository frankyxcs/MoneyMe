package com.devmoroz.moneyme.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.devmoroz.moneyme.utils.FormatUtils;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;
import java.util.UUID;

@DatabaseTable(tableName = "goals")
public class Goal implements Parcelable{

    public static final Parcelable.Creator<Goal> CREATOR = new Parcelable.Creator<Goal>() {
        public Goal createFromParcel(Parcel in) {
            return new Goal(in);
        }

        public Goal[] newArray(int size) {
            return new Goal[size];
        }
    };

    @DatabaseField(generatedId = true)
    private UUID id;

    @DatabaseField(canBeNull = false)
    private String name;

    @DatabaseField(canBeNull = true)
    private String notes;

    @DatabaseField(dataType = DataType.DATE)
    private Date deadlineDate;

    @DatabaseField
    private int totalAmount;

    @DatabaseField
    private int accumulated;

    @DatabaseField(dataType = DataType.BOOLEAN)
    private boolean achieved;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Payee payee;

    public Goal(Parcel parcel) {
        setId(parcel.readString());
        setName(parcel.readString());
        setNotes(parcel.readString());
        setDeadlineDateLong(parcel.readLong());
        setTotalAmount(parcel.readInt());
        setAccumulated(parcel.readInt());
        setAchieved(parcel.readInt() != 0);
        setPayee(parcel.readParcelable(Payee.class.getClassLoader()));
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(getId());
        parcel.writeString(name);
        parcel.writeString(notes);
        parcel.writeLong(getDeadLong());
        parcel.writeInt(totalAmount);
        parcel.writeInt(accumulated);
        parcel.writeInt(achieved ? 1 : 0);
        parcel.writeParcelable(payee,flags);
    }

    public Goal() {
    }

    public String getId() {
        return id != null ? id.toString() : null;
    }

    public String getName() {
        return name;
    }

    public String getNotes() {
        return notes;
    }

    public Date getDeadlineDate() {
        return deadlineDate;
    }

    public Long getDeadLong() {
        return deadlineDate.getTime();
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public int getAccumulated() {
        return accumulated;
    }

    public void setAccumulated(int accumulated) {
        this.accumulated = accumulated;
    }



    public boolean isAchieved() {
        return achieved;
    }

    public Payee getPayee() {
        return payee;
    }

    public void setPayee(Payee payee) {
        this.payee = payee;
    }

    public void setId(String id) {
        if (FormatUtils.isNotEmpty(id)) {
            this.id = UUID.fromString(id);
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setDeadlineDate(Date deadlineDate) {
        this.deadlineDate = deadlineDate;
    }

    public void setDeadlineDateLong(long deadlineDate) {
        if(deadlineDate!=0){
        this.deadlineDate = new Date(deadlineDate);}
    }

    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }


    public void setAchieved(boolean achieved) {
        this.achieved = achieved;
    }

    public Goal(String name, String notes, Date deadlineDate, int totalAmount, int accumulated) {
        this.name = name;
        this.notes = notes;
        this.deadlineDate = deadlineDate;
        this.totalAmount = totalAmount;
        this.accumulated = accumulated;
    }
}
