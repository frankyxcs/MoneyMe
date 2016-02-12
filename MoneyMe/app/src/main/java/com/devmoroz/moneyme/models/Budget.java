package com.devmoroz.moneyme.models;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.UUID;

@DatabaseTable(tableName = "budgets")
public class Budget {

    @DatabaseField(generatedId = true)
    UUID id;

    @DatabaseField(canBeNull = false)
    int budget_year;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, canBeNull = true)
    Category category;

    @DatabaseField(canBeNull = false)
    double amount;

    @DatabaseField(canBeNull = false)
    double spent;

    @DatabaseField(canBeNull = false)
    BudgetPeriodType period;

    @DatabaseField(foreign = true,foreignAutoRefresh = true, canBeNull = true)
    Payee payee;

    public Budget() {
    }
}
