package com.devmoroz.moneyme.models;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.Date;

@DatabaseTable(tableName = "goals")
public class Goal {

    @DatabaseField(generatedId = true)
    private int Id;

    private String Name;

    @DatabaseField(dataType = DataType.DATE)
    private Date deadlineDate;

    public Goal() {
    }
}
