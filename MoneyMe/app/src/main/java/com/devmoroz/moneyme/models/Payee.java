package com.devmoroz.moneyme.models;


import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.UUID;

@DatabaseTable(tableName = "payees")
public class Payee {

    @DatabaseField(generatedId = true)
    private UUID id;

    @DatabaseField
    private String name;

    @DatabaseField
    private String icon;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Currency currency;

    @ForeignCollectionField
    private ForeignCollection<Transaction> transactions;

    @ForeignCollectionField
    private ForeignCollection<Budget> budgets;

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

    public ForeignCollection<Transaction> getTransactions() {
        return transactions;
    }

    public String getId() {
        return id.toString();
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
}
