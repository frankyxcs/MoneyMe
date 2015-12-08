package com.devmoroz.moneyme.models;


import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExportImportModel {

    int type;
    String amount;
    String category;
    Date date;
    String account;
    String accountType;
    String notes;
    String location;

    public ExportImportModel(int type, String amount, String category, Date date, String account, String accountType, String notes, String location) {
        this.type = type;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.account = account;
        this.accountType = accountType;
        this.notes = notes;
        this.location = location;
    }

    public int getType() {
        return type;
    }

    public String getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public Date getDate() {
        return date;
    }

    public String getAccount() {
        return account;
    }

    public String getAccountType() {
        return accountType;
    }

    public String getNotes() {
        return notes;
    }

    public String getLocation() {
        return location;
    }

    public static ArrayList<ExportImportModel> convertFromEntities(List<Income> incomes, List<Outcome> outcomes){
        ArrayList<ExportImportModel> list = new ArrayList<>();
        try {
            if (incomes != null) {
                for (Income in : incomes) {
                    list.add(convertFromIncome(in));
                }
            }
            if (outcomes != null) {
                for (Outcome out : outcomes) {
                    list.add(convertFromOutcome(out));
                }
            }
        }
        catch (Exception ex){

        }
        return list;
    }

    public static ExportImportModel convertFromIncome(Income income){
        DecimalFormat format = getFormat();
        ExportImportModel model = new ExportImportModel(1,format.format(income.getAmount()),"",income.getDateOfReceipt(),income.getAccountName(),String.valueOf(income.getAccount().getType()),income.getNotes(),"");
        return model;
    }

    public static ExportImportModel convertFromOutcome(Outcome outcome){
        DecimalFormat format = getFormat();
        ExportImportModel model = new ExportImportModel(2, format.format(outcome.getAmount()),outcome.getCategory(),outcome.getDateOfSpending(),outcome.getAccountName(),String.valueOf(outcome.getAccount().getType()),outcome.getNotes(),outcome.getLocation());
        return model;
    }

    public static DecimalFormat getFormat(){
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(' ');
        symbols.setDecimalSeparator('.');
        DecimalFormat decimalFormat = new DecimalFormat("# ##0.00", symbols);
        decimalFormat.setGroupingSize(3);

        return decimalFormat;
    }
}
