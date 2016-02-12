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

    public Budget(String id, int budget_year, Category category, double amount, double spent, BudgetPeriodType period) {
        this.id = UUID.fromString(id);
        this.budget_year = budget_year;
        this.category = category;
        this.amount = amount;
        this.spent = spent;
        this.period = period;
    }

    public String getId() {
        return id.toString();
    }

    public void setId(String id) {
        this.id = UUID.fromString(id);
    }

    public int getBudgetYear() {
        return budget_year;
    }

    public void setBudgetYear(int budget_year) {
        this.budget_year = budget_year;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getSpent() {
        return spent;
    }

    public void setSpent(double spent) {
        this.spent = spent;
    }

    public BudgetPeriodType getPeriod() {
        return period;
    }

    public void setPeriod(BudgetPeriodType period) {
        this.period = period;
    }

    public Payee getPayee() {
        return payee;
    }

    public void setPayee(Payee payee) {
        this.payee = payee;
    }
}
