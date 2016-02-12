package com.devmoroz.moneyme.export.backup;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.devmoroz.moneyme.export.Exporter;
import com.devmoroz.moneyme.models.Account;
import com.devmoroz.moneyme.models.Budget;
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

    private List<Transaction> transactions;
    private List<Account> accounts;
    private List<Budget> budgets;

    public DBExport(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public void generateExport(OutputStream outputStream) throws Exception{
        final JsonWriter writer = new JsonWriter(new OutputStreamWriter(outputStream, CHARSET_NAME));
        writer.setIndent("  ");

        writer.beginObject();

        writer.name("budgets").beginArray();
        writeBudgets(writer);
        writer.endArray();

        writer.name("accounts").beginArray();
        writeAccounts(writer);
        writer.endArray();

        writer.name("transactions").beginArray();
        writeTransactions(writer);
        writer.endArray();

        writer.endObject();
        writer.close();
    }

    private void writeMetaData(JsonWriter writer) throws IOException {
        writer.name("version").value(VERSION);
        writer.name("timestamp").value(System.currentTimeMillis());
    }

    private void writeBudgets(JsonWriter writer) throws IOException {
        try {


        } finally {

        }
    }

    private void writeAccounts(JsonWriter writer) throws IOException {
        try {

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
                writer.endObject();
            }
        } finally {

        }
    }

    @Override
    protected String getExtension() {
        return ".mmbackup";
    }


}
