package com.devmoroz.moneyme.models;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

@DatabaseTable(tableName = "incomes")
public class Income {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = true,dataType = DataType.STRING)
    private String name;

    @DatabaseField(canBeNull = true)
    private String notes;

    @DatabaseField(canBeNull = true)
    private String type;

    @DatabaseField(dataType = DataType.DATE)
    private Date dateOfReceipt;

    @DatabaseField(canBeNull = false,dataType = DataType.DOUBLE)
    private double amount;

    @DatabaseField(canBeNull = true)
    private int currency;

    public Income() {
    }

    public String getIncomeType() {
        return type;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
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

    public Income(String name, String notes, String type, Date dateOfReceipt, double amount, int currency) {
        this.name = name;
        this.notes = notes;
        this.type = type;
        this.dateOfReceipt = dateOfReceipt;
        this.amount = amount;
        this.currency = currency;
    }
}
