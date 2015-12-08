package com.devmoroz.moneyme.models;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

@DatabaseTable(tableName = "outcomes")
public class Outcome {

    public static final String ACCOUNT_ID_FIELD_NAME = "account_id";

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = true)
    private String notes;

    @DatabaseField(canBeNull = false)
    private String category;

    @DatabaseField(dataType = DataType.DATE)
    private Date dateOfSpending;

    @DatabaseField(canBeNull = false,dataType = DataType.DOUBLE)
    private double amount;

    @DatabaseField
    private String photo;

    @DatabaseField
    private String location;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = ACCOUNT_ID_FIELD_NAME)
    private Account account;

    public Outcome() {
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getCategory() {
        return category;
    }

    public int getId() {
        return id;
    }

    public String getNotes() {
        return notes;
    }

    public Date getDateOfSpending() {
        return dateOfSpending;
    }

    public double getAmount() {
        return amount;
    }

    public Account getAccount() {
        return account;
    }

    public String getAccountName() { return account.getName();}

    public String getLocation() {
        return location;
    }

    public Outcome(String notes, Date dateOfSpending, double amount, String category,Account account) {
        this.notes = notes;
        this.dateOfSpending = dateOfSpending;
        this.amount = amount;
        this.category = category;
        this.account = account;
    }
}
