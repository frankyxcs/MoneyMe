package com.devmoroz.moneyme.models;


public class AccountRow {

    public String id;

    public String name;

    public double total;

    public double expense;

    public int type;


    public AccountRow(String id, String name, double total, double expense, int type) {
        this.id = id;
        this.name = name;
        this.total = total;
        this.expense = expense;
        this.type = type;
    }
}
