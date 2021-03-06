package com.devmoroz.moneyme.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.devmoroz.moneyme.utils.FormatUtils;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;
import java.util.UUID;

@DatabaseTable(tableName = "accounts")
public class Account implements Parcelable{

    public static final Parcelable.Creator<Account> CREATOR = new Parcelable.Creator<Account>() {
        public Account createFromParcel(Parcel in) {
            return new Account(in);
        }

        public Account[] newArray(int size) {
            return new Account[size];
        }
    };

    public final static String INCLUDE_IN_TOTAL_FIELD_NAME = "includeintotal";

    public Account() {
    }

    @DatabaseField(generatedId = true)
    private UUID id;

    @DatabaseField
    private String name;

    @DatabaseField
    private double balance;

    @DatabaseField
    private String currency;

    @DatabaseField
    private int type;

    @DatabaseField(columnName = INCLUDE_IN_TOTAL_FIELD_NAME)
    private boolean includeInTotal;

    @DatabaseField
    private boolean shared;

    @DatabaseField
    private boolean deleted;

    @ForeignCollectionField
    private ForeignCollection<Transaction> transactions;

    public Account(Parcel parcel) {
        setId(parcel.readString());
        setName(parcel.readString());
        setBalance(parcel.readDouble());
        setCurrency(parcel.readString());
        setType(parcel.readInt());
        setIncludeInTotal(parcel.readInt() != 0);
        setShared(parcel.readInt() != 0);
        setDeleted(parcel.readInt() != 0);
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(getId());
        parcel.writeString(name);
        parcel.writeDouble(balance);
        parcel.writeString(currency);
        parcel.writeInt(type);
        parcel.writeInt(includeInTotal ? 1 : 0);
        parcel.writeInt(shared ? 1 : 0);
        parcel.writeInt(deleted ? 1 : 0);
    }


    public Account(String name, double balance, int type, boolean shared) {
        this.name = name;
        this.balance = balance;
        this.includeInTotal = true;
        this.type = type;
        this.shared = shared;
    }

    public Account(String id) {
        this.id = UUID.fromString(id);
        this.balance = 0f;
        this.type = 0;
        this.includeInTotal = true;
        this.shared = true;
    }

    public Account(String name, double balance, String currency, int type, boolean includeInTotal, boolean shared) {
        this.name = name;
        this.balance = balance;
        this.currency = currency;
        this.includeInTotal = includeInTotal;
        this.type = type;
        this.shared = shared;
    }

    public Account(String id, String name, double balance, String currency, int type, boolean includeInTotal, boolean shared) {
        this.id = UUID.fromString(id);
        this.name = name;
        this.balance = balance;
        this.currency = currency;
        this.type = type;
        this.includeInTotal = includeInTotal;
        this.shared = shared;
    }

    public boolean isShared() {
        return shared;
    }

    public void setShared(boolean shared) {
        this.shared = shared;
    }

    public String getId() {
        return id != null ? id.toString() : null;
    }

    public void setId(String id) {
        if (FormatUtils.isNotEmpty(id)) {
            this.id = UUID.fromString(id);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public ForeignCollection<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(ForeignCollection<Transaction> transactions) {
        this.transactions = transactions;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isIncludeInTotal() {
        return includeInTotal;
    }

    public void setIncludeInTotal(boolean includeInTotal) {
        this.includeInTotal = includeInTotal;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account)) return false;

        final Account model = (Account) o;

        // We are only checking id, because otherwise some parts of the app might misbehave
        // For example BaseModelAdapter contains Set<BaseModel> selectedItems
        // noinspection RedundantIfStatement
        return !(FormatUtils.isEmpty(getId()) || FormatUtils.isEmpty(model.getId())) && id.equals(model.id);

    }

    @Override public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
