package com.devmoroz.moneyme.models;

public class MyCurrency {

    private String symbol;
    private String desc;
    private int pos;

    public MyCurrency(String symbol, String desc) {
        this.symbol = symbol;
        this.desc = desc;
    }

    public String getSymbol() {
        return this.symbol;
    }

    public String getDesc() {
        return this.desc;
    }
}
