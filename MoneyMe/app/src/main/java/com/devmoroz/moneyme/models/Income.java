package com.devmoroz.moneyme.models;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

@DatabaseTable(tableName = "incomes")
public class Income {

    public static final String ACCOUNT_ID_FIELD_NAME = "account_id";

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = true)
    private String notes;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = ACCOUNT_ID_FIELD_NAME)
    private Account account;

    @DatabaseField(dataType = DataType.DATE)
    private Date dateOfReceipt;

    @DatabaseField(canBeNull = false,dataType = DataType.DOUBLE)
    private double amount;

    public Income() {
    }

    public int getId() {
        return id;
    }

    public String getNotes() {
        return notes;
    }

    public Date getDateOfReceipt() {
        return dateOfReceipt;
    }

    public double getAmount() {
        return amount;
    }

    public Account getAccount() {
        return account;
    }

    public String getAccountName() { return account.getName();}

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setDateOfReceipt(Date dateOfReceipt) {
        this.dateOfReceipt = dateOfReceipt;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Income(String notes, Date dateOfReceipt, double amount, Account account) {
        this.notes = notes;
        this.account = account;
        this.dateOfReceipt = dateOfReceipt;
        this.amount = amount;
    }
}
