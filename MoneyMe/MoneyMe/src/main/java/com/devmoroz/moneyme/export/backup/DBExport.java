package com.devmoroz.moneyme.export.backup;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.devmoroz.moneyme.MoneyApplication;
import com.devmoroz.moneyme.export.Exporter;
import com.devmoroz.moneyme.helpers.DBHelper;
import com.devmoroz.moneyme.models.Account;
import com.devmoroz.moneyme.models.Budget;
import com.devmoroz.moneyme.models.Category;
import com.devmoroz.moneyme.models.Currency;
import com.devmoroz.moneyme.models.Goal;
import com.devmoroz.moneyme.models.Payee;
import com.devmoroz.moneyme.models.Transaction;
import com.devmoroz.moneyme.utils.AppUtils;
import com.google.gson.stream.JsonWriter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;


public class DBExport extends Exporter{
    public static final int VERSION = 1;

    private static final String CHARSET_NAME = "UTF-8";

    private final Context context;
    private DBHelper dbHelper;

    private List<Transaction> transactions;
    private List<Account> accounts;
    private List<Budget> budgets;
    private List<Category> categories;
    private List<Goal> goals;
    private List<Payee> payees;
    private List<Currency> currencies;

    public DBExport(Context context, OutputStream outputStream) {
        super(context,outputStream);
        this.context = context;
    }

    @Override
    public void generateExport(OutputStream outputStream) throws Exception{
        dbHelper = MoneyApplication.GetDBHelper();

        transactions = dbHelper.getTransactionDAO().queryForAll();
        accounts = dbHelper.getAccountDAO().queryForAll();
        budgets = dbHelper.getBudgetDAO().queryForAll();
        categories = dbHelper.getCategoryDAO().queryForAll();
        goals = dbHelper.getGoalDAO().queryForAll();
        payees = dbHelper.getPayeeDAO().queryForAll();
        currencies = dbHelper.getCurrencyDAO().queryForAll();

        final JsonWriter writer = new JsonWriter(new OutputStreamWriter(outputStream, CHARSET_NAME));
        writer.setIndent("  ");

        writer.beginObject();

        writeMetaData(writer);

        writer.name("currencies").beginArray();
        writeCurrencies(writer);
        writer.endArray();

        writer.name("payees").beginArray();
        writePayees(writer);
        writer.endArray();

        writer.name("accounts").beginArray();
        writeAccounts(writer);
        writer.endArray();

        writer.name("categories").beginArray();
        writeCategories(writer);
        writer.endArray();

        writer.name("budgets").beginArray();
        writeBudgets(writer);
        writer.endArray();

        writer.name("transactions").beginArray();
        writeTransactions(writer);
        writer.endArray();

        writer.name("goals").beginArray();
        writeGoals(writer);
        writer.endArray();

        writer.endObject();
        writer.close();
    }

    private void writeMetaData(JsonWriter writer) throws IOException {
        writer.name("version").value(VERSION);
        writer.name("timestamp").value(System.currentTimeMillis());
    }

    private void writeCategories(JsonWriter writer) throws IOException {
        try {
            for(Category c: categories){
                writer.beginObject();
                writer.name("category_id").value(c.getId());
                writer.name("title").value(c.getTitle());
                writer.name("color").value(c.getColor());
                writer.name("order").value(c.getOrder());
                writer.name("custom").value(c.isCustom());
                writer.endObject();
            }
        } finally {

        }
    }

    private void writeCurrencies(JsonWriter writer) throws IOException {
        try {
            for(Currency c: currencies){
                writer.beginObject();
                writer.name("currency_id").value(c.getId());
                writer.name("name").value(c.getName());
                writer.name("title").value(c.getTitle());
                writer.name("symbol").value(c.getSymbol());
                writer.endObject();
            }
        } finally {

        }
    }

    private void writeBudgets(JsonWriter writer) throws IOException {
        try {
            for(Budget b: budgets){
                writer.beginObject();
                writer.name("budget_id").value(b.getId());
                writer.name("budget_year").value(b.getBudgetYear());
                writer.name("category_id").value(b.getCategory().getId());
                writer.name("amount").value(b.getAmount());
                writer.name("spent").value(b.getSpent());
                writer.name("period_type").value(b.getPeriod().asInt());
                writer.name("payee_id").value(b.getPayee().getId());
                writer.endObject();
            }
        } finally {

        }
    }

    private void writeAccounts(JsonWriter writer) throws IOException {
        try {
            for(Account a: accounts){
                writer.beginObject();
                writer.name("account_id").value(a.getId());
                writer.name("name").value(a.getName());
                writer.name("balance").value(a.getBalance());
                writer.name("type").value(a.getType());
                writer.name("shared").value(a.isShared());
                writer.name("currency_id").value(a.getCurrency());
                writer.endObject();
            }
        } finally {

        }
    }

    private void writeGoals(JsonWriter writer) throws IOException {
        try {
            for(Goal g: goals){
                writer.beginObject();
                writer.name("goal_id").value(g.getId());
                writer.name("name").value(g.getName());
                writer.name("notes").value(g.getNotes());
                writer.name("deadline_date").value(g.getDeadLong());
                writer.name("total_amount").value(g.getTotalAmount());
                writer.name("accumulated").value(g.getAccumulated());
                writer.name("payee_id").value(g.getPayee().getId());
                writer.endObject();
            }
        } finally {

        }
    }

    private void writeTransactions(JsonWriter writer) throws IOException {
        try {
            for(Transaction t: transactions){
                writer.beginObject();
                writer.name("account_id").value(t.getAccount().getId());
                writer.name("transaction_type").value(t.getType().asInt());
                writer.name("category_id").value(t.getCategory() != null ? t.getCategory().getId() : null);
                writer.name("date").value(t.getDateLong());
                writer.name("amount").value(t.getAmount());
                writer.name("note").value(t.getNotes());
                writer.name("photo").value(t.getTags());
                writer.name("location").value(t.getLocation() != null ? t.getLocation() : null);
                writer.name("tags").value(t.getTags());
                writer.name("sync_state").value(t.getSyncState().asInt());
                writer.name("payee_id").value(t.getPayee().getId());
                writer.endObject();
            }
        } finally {

        }
    }

    private void writePayees(JsonWriter writer) throws IOException {
        try {
            for(Payee p: payees){
                writer.beginObject();
                writer.name("payee_id").value(p.getId());
                writer.name("name").value(p.getName());
                writer.name("icon").value(p.getIcon());
                writer.name("currency_id").value(p.getCurrency().getId());
                writer.endObject();
            }
        } finally {

        }
    }

    @Override
    protected String getExtension() {
        return ".json";
    }


}
