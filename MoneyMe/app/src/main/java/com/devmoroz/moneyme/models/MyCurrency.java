package com.devmoroz.moneyme.models;

public class MyCurrency {

    private String desc;
    private int id;

    public MyCurrency(int id, String desc) {
        this.id = id;
        this.desc = desc;
    }

    public int getId() {
        return this.id;
    }
    public String getDesc() {
        return this.desc;
    }
}
