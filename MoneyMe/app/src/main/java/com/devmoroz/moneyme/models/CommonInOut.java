package com.devmoroz.moneyme.models;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

public class CommonInOut implements Parcelable{

    public static final Parcelable.Creator<CommonInOut> CREATOR
            = new Parcelable.Creator<CommonInOut>() {
        public CommonInOut createFromParcel(Parcel in) {

            return new CommonInOut(in);
        }

        public CommonInOut[] newArray(int size) {
            return new CommonInOut[size];
        }
    };

    private DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

    int type;
    int id;
    double amount;
    String description;
    Date dateAdded;

    public CommonInOut(Parcel input) {
        id = input.readInt();
        type = input.readInt();
        amount = input.readDouble();
        description = input.readString();
        long dateMillis=input.readLong();
        dateAdded = (dateMillis == -1 ? null : new Date(dateMillis));

    }

    public CommonInOut(int type, int id, double amount, String description, Date dateAdded) {
        this.type = type;
        this.id = id;
        this.amount = amount;
        this.description = description;
        this.dateAdded = dateAdded;
    }

    public int getType() {
        return type;
    }

    public int getId() {
        return id;
    }

    public double getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }
    public String getFormatedDate() {
        return dateFormat.format(dateAdded);
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(type);
        dest.writeDouble(amount);
        dest.writeString(description);
        dest.writeLong(dateAdded == null ? -1 : dateAdded.getTime());
    }
}
