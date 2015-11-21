package com.devmoroz.moneyme.models;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

@DatabaseTable(tableName = "accounts")
public class Account {

    public final static String INCLUDE_IN_TOTAL_FIELD_NAME = "includeintotal";

    public Account() {
    }

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private String name;

    @DatabaseField
    private double balance;

    @DatabaseField(dataType = DataType.DATE)
    private Date date;

    @DatabaseField
    private int currency;

    @DatabaseField(columnName = INCLUDE_IN_TOTAL_FIELD_NAME)
    private boolean includeInTotal;

    @ForeignCollectionField
    private ForeignCollection<Income> incomes;

    @ForeignCollectionField
    private ForeignCollection<Outcome> outcomes;

    public Account(String name, double balance, boolean includeInTotal) {
        this.name = name;
        this.balance = balance;
        this.includeInTotal = includeInTotal;
        this.date = new Date();
    }

    public Account(int id) {
        this.id = id;
        this.balance = 0f;
        this.date = new Date();
    }

    public Account(String name, double balance, int currency, boolean includeInTotal) {
        this.name = name;
        this.balance = balance;
        this.currency = currency;
        this.includeInTotal = includeInTotal;
        this.date = new Date();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getCurrency() {
        return currency;
    }

    public void setCurrency(int currency) {
        this.currency = currency;
    }

    public ForeignCollection<Income> getIncomes() {
        return incomes;
    }

    public void setIncomes(ForeignCollection<Income> incomes) {
        this.incomes = incomes;
    }

    public ForeignCollection<Outcome> getOutcomes() {
        return outcomes;
    }

    public void setOutcomes(ForeignCollection<Outcome> outcomes) {
        this.outcomes = outcomes;
    }

    public boolean isIncludeInTotal() {
        return includeInTotal;
    }

    public void setIncludeInTotal(boolean includeInTotal) {
        this.includeInTotal = includeInTotal;
    }
}
