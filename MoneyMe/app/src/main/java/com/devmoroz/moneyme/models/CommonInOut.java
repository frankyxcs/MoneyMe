package com.devmoroz.moneyme.models;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CommonInOut {

    private DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

    int type;
    int id;
    String amount;
    String description;
    Date dateAdded;

    public CommonInOut(int type, int id, String amount, String description, Date dateAdded) {
        this.type = type;
        this.id = id;
        this.amount = amount;
        this.description = description;
        this.dateAdded = dateAdded;
    }

    public int getType() {
        return type;
    }

    public int getId() {
        return id;
    }

    public String getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }
    public Date getFormatedDate() {
        try{
            return dateFormat.parse(dateAdded.toString());
        }
        catch (ParseException ex){
            return  new Date();
        }
    }
}
