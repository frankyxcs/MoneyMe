package com.devmoroz.moneyme.models;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "currency")
public class Currency {

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
}
