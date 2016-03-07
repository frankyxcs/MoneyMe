package com.devmoroz.moneyme.models;


import android.os.Parcel;
import android.os.Parcelable;

import com.devmoroz.moneyme.utils.FormatUtils;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.UUID;

@DatabaseTable(tableName = "payees")
public class Payee implements Parcelable{

    public static final Parcelable.Creator<Payee> CREATOR = new Parcelable.Creator<Payee>() {
        public Payee createFromParcel(Parcel in) {
            return new Payee(in);
        }

        public Payee[] newArray(int size) {
            return new Payee[size];
        }
    };

    @DatabaseField(generatedId = true)
    private UUID id;

    @DatabaseField
    private String name;

    @DatabaseField
    private String icon;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Currency currency;


    public Payee(Parcel parcel) {
        setId(parcel.readString());
        setName(parcel.readString());
        setIcon(parcel.readString());
        setCurrency(parcel.readParcelable(Currency.class.getClassLoader()));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(getId());
        parcel.writeString(getName());
        parcel.writeString(getIcon());
        parcel.writeParcelable(currency, flags);
    }

    public Payee() {
    }

    public Payee(String name) {
        this.name = name;
    }

    public Payee(String id, String name) {
        this.id = UUID.fromString(id);
        this.name = name;
    }

    public Payee(UUID id, String name, String icon) {
        this.id = id;
        this.name = name;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id != null ? id.toString() : null;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setId(String id) {
        if (FormatUtils.isNotEmpty(id)) {
            this.id = UUID.fromString(id);
        }
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }
}
