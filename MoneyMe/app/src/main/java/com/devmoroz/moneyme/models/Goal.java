package com.devmoroz.moneyme.models;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;
import java.util.UUID;

@DatabaseTable(tableName = "goals")
public class Goal {

    @DatabaseField(generatedId = true)
    private UUID id;

    @DatabaseField(canBeNull = false,dataType = DataType.STRING)
    private String name;

    @DatabaseField(canBeNull = true)
    private String notes;

    @DatabaseField(dataType = DataType.DATE)
    private Date deadlineDate;

    @DatabaseField(canBeNull = false,dataType = DataType.INTEGER)
    private int totalAmount;

    @DatabaseField(canBeNull = true,dataType = DataType.INTEGER)
    private int accumulated;

    @DatabaseField(canBeNull = false)
    private int currency;

    @DatabaseField(dataType = DataType.BOOLEAN)
    private boolean achieved;


    public Goal() {
    }

    public String getId() {
        return id.toString();
    }

    public String getName() {
        return name;
    }

    public String getNotes() {
        return notes;
    }

    public Date getDeadlineDate() {
        return deadlineDate;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public int getAccumulated() {
        return accumulated;
    }

    public void setAccumulated(int accumulated) {
        this.accumulated = accumulated;
    }

    public int getCurrency() {
        return currency;
    }

    public boolean isAchieved() {
        return achieved;
    }

    public Goal(String name, String notes, Date deadlineDate, int totalAmount, int accumulated) {
        this.name = name;
        this.notes = notes;
        this.deadlineDate = deadlineDate;
        this.totalAmount = totalAmount;
        this.accumulated = accumulated;
    }
}
