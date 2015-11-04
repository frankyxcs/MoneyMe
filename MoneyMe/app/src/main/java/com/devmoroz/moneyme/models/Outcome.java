package com.devmoroz.moneyme.models;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

@DatabaseTable(tableName = "outcomes")
public class Outcome {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = true,dataType = DataType.STRING)
    private String name;

    @DatabaseField(canBeNull = true)
    private String notes;

    @DatabaseField(canBeNull = false)
    private String category;

    @DatabaseField(dataType = DataType.DATE)
    private Date dateOfSpending;

    @DatabaseField(canBeNull = false,dataType = DataType.DOUBLE)
    private double amount;

    public String getCategory() {
        return category;
    }

    @DatabaseField(canBeNull = false)
    private int currency;

    public Outcome() {
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

    public Date getDateOfSpending() {
        return dateOfSpending;
    }

    public double getAmount() {
        return amount;
    }

    public int getCurrency() {
        return currency;
    }

    public Outcome(String name, String notes, Date dateOfSpending, double amount, String category,int currency) {
        this.name = name;
        this.notes = notes;
        this.dateOfSpending = dateOfSpending;
        this.amount = amount;
        this.category = category;
        this.currency = currency;
    }
}
