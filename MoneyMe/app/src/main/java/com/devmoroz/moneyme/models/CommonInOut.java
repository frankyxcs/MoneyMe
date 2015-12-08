package com.devmoroz.moneyme.models;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

import com.devmoroz.moneyme.MoneyApplication;
import com.devmoroz.moneyme.utils.CurrencyCache;

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
    String category;
    Date dateAdded;
    String account;
    String photo;
    String notes;
    String location;

    public CommonInOut(Parcel input) {
        id = input.readInt();
        type = input.readInt();
        amount = input.readDouble();
        category = input.readString();
        long dateMillis=input.readLong();
        dateAdded = (dateMillis == -1 ? null : new Date(dateMillis));
        account = input.readString();
        photo = input.readString();
        notes = input.readString();
        location = input.readString();
    }

    public String getAccount() {
        return account;
    }

    public CommonInOut(int type, int id, double amount, String category, Date dateAdded, String account, String photo, String notes) {
        this.type = type;
        this.id = id;
        this.amount = amount;
        this.dateAdded = dateAdded;
        this.account = account;
        this.category = category;
        this.photo = photo;
        this.notes = notes;
    }

    public int getType() {
        return type;
    }

    public int getId() {
        return id;
    }


    public String getCategory() {
        return category;
    }

    public double getAmount() {
        return amount;
    }

    public String getFormatedDate() {
        return dateFormat.format(dateAdded);
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public long getDateLong(){ return dateAdded.getTime();}

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getNotes() {
        return notes;
    }

    public String getLocation() {return location;}

    public String getFormatedAmount() {
        String sign = CurrencyCache.getCurrencyOrEmpty().getSymbol();

        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(',');
        symbols.setDecimalSeparator('.');

        DecimalFormat decimalFormat = new DecimalFormat("#,###.00 " + sign, symbols);
        decimalFormat.setGroupingSize(3);

        return decimalFormat.format(amount);
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
        dest.writeString(category);
        dest.writeLong(dateAdded == null ? -1 : dateAdded.getTime());
        dest.writeString(account);
        dest.writeString(photo);
        dest.writeString(notes);
        dest.writeString(location);
    }
}
