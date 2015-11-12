package com.devmoroz.moneyme.models;


public class AccountRow {

    public int id;

    public String name;

    public double total;

    public double expense;


    public AccountRow(int id, String name, double total, double expense) {
        this.id = id;
        this.name = name;
        this.total = total;
        this.expense = expense;
    }
}
