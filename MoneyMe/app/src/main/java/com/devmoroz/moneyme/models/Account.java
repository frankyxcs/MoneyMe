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

    @DatabaseField
    private int type;

    @DatabaseField(columnName = INCLUDE_IN_TOTAL_FIELD_NAME)
    private boolean includeInTotal;

    @ForeignCollectionField
    private ForeignCollection<Transaction> transactions;

    public Account(String name, double balance, int type) {
        this.name = name;
        this.balance = balance;
        this.includeInTotal = true;
        this.date = new Date();
        this.type = type;
    }

    public Account(int id) {
        this.id = id;
        this.balance = 0f;
        this.type = 0;
        this.includeInTotal = true;
        this.date = new Date();
    }

    public Account(String name, double balance, int currency, int type, boolean includeInTotal) {
        this.name = name;
        this.balance = balance;
        this.currency = currency;
        this.includeInTotal = includeInTotal;
        this.type = type;
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

    public ForeignCollection<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(ForeignCollection<Transaction> transactions) {
        this.transactions = transactions;
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
}
