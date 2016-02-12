package com.devmoroz.moneyme.models;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.UUID;

@DatabaseTable(tableName = "currency")
public class Currency {

    public static final Currency EMPTY = new Currency();

    static {
        EMPTY.id = "";
        EMPTY.name = "";
        EMPTY.title = "Default";
        EMPTY.symbol = "";
    }


    @DatabaseField(generatedId = false)
    private String id;

    @DatabaseField
    private String name;

    @DatabaseField
    private String title;

    @DatabaseField
    private String symbol;

    @DatabaseField
    private boolean isDeafult;

    public Currency() {
        this.id = UUID.randomUUID().toString();
    }

    public Currency(String name, String title, String symbol) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.title = title;
        this.symbol = symbol;
    }

    public String getId() {
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

    public void setId(String id) {
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
        return id.isEmpty();
    }

    public DecimalFormat getFormat(){
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(',');
        symbols.setDecimalSeparator('.');

        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00", symbols);
        decimalFormat.setGroupingSize(3);

        return decimalFormat;
    }
}
