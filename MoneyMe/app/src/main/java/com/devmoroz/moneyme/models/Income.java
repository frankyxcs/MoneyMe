package com.devmoroz.moneyme.models;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

@DatabaseTable(tableName = "incomes")
public class Income {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = true)
    private String notes;

    @DatabaseField(canBeNull = true)
    private String account;

    @DatabaseField(dataType = DataType.DATE)
    private Date dateOfReceipt;

    @DatabaseField(canBeNull = false,dataType = DataType.DOUBLE)
    private double amount;

    @DatabaseField(canBeNull = true)
    private int currency;

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

    public int getCurrency() {
        return currency;
    }

    public String getAccount() {
        return account;
    }

    public Income(String notes, String account, Date dateOfReceipt, double amount, int currency) {
        this.notes = notes;
        this.account = account;
        this.dateOfReceipt = dateOfReceipt;
        this.amount = amount;
        this.currency = currency;
    }
}
