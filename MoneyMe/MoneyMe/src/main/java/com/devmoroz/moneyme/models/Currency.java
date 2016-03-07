package com.devmoroz.moneyme.models;


import android.os.Parcel;
import android.os.Parcelable;

import com.devmoroz.moneyme.utils.FormatUtils;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.UUID;

@DatabaseTable(tableName = "currencies")
public class Currency implements Parcelable {

    public static final Parcelable.Creator<Currency> CREATOR = new Parcelable.Creator<Currency>() {
        public Currency createFromParcel(Parcel in) {
            return new Currency(in);
        }

        public Currency[] newArray(int size) {
            return new Currency[size];
        }
    };

    public static final Currency EMPTY = new Currency();

    static {
        EMPTY.id = UUID.randomUUID();
        EMPTY.name = "";
        EMPTY.title = "Default";
        EMPTY.symbol = "";
    }


    @DatabaseField(generatedId = true)
    private UUID id;

    @DatabaseField
    private String name;

    @DatabaseField
    private String title;

    @DatabaseField
    private String symbol;

    public Currency(Parcel parcel) {
        setId(parcel.readString());
        setName(parcel.readString());
        setTitle(parcel.readString());
        setSymbol(parcel.readString());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(getId());
        parcel.writeString(getName());
        parcel.writeString(getTitle());
        parcel.writeString(getSymbol());
    }

    public Currency() {

    }

    public Currency(String name, String title, String symbol) {
        this.name = name;
        this.title = title;
        this.symbol = symbol;
    }

    public String getId() {
        return id != null ? id.toString() : null;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setId(String id) {
        if (FormatUtils.isNotEmpty(id)) {
            this.id = UUID.fromString(id);
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public boolean isEmpty() {
        return title.equals("Default");
    }

    public DecimalFormat getFormat() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(',');
        symbols.setDecimalSeparator('.');

        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00", symbols);
        decimalFormat.setGroupingSize(3);

        return decimalFormat;
    }
}
