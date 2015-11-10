package com.devmoroz.moneyme.models;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "currency")
public class Currency {

    public static final Currency EMPTY = new Currency();

    static {
        EMPTY.id = -1;
        EMPTY.name = "";
        EMPTY.title = "Default";
        EMPTY.symbol = "";
    }


    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private String name;

    @DatabaseField
    private String title;

    @DatabaseField
    private String symbol;

    @DatabaseField
    private boolean isDeafult;

    public Currency() {
    }

    public Currency(String name, String title, String symbol) {
        this.name = name;
        this.title = title;
        this.symbol = symbol;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setIsDeafult(boolean isDeafult) {
        this.isDeafult = isDeafult;
    }

    public boolean isDeafult() {

        return isDeafult;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public boolean isEmpty() {
        return id == -1;

    }
}
