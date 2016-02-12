package com.devmoroz.moneyme.export;


import android.content.Context;

import com.devmoroz.moneyme.MoneyApplication;
import com.devmoroz.moneyme.helpers.DBHelper;
import com.devmoroz.moneyme.models.Currency;
import com.devmoroz.moneyme.models.Transaction;
import com.devmoroz.moneyme.utils.CurrencyCache;
import com.devmoroz.moneyme.utils.csv.Csv;
import com.devmoroz.moneyme.utils.csv.CsvExportOptions;

import org.joda.time.DateTime;

import java.io.BufferedWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class CSVexport  {

    public static final String[] HEADER = "type,date,account,accountType,amount,currency,category,location,note,payee".split(",");
    public static final DateFormat FORMAT_DATE_ISO_8601 = new SimpleDateFormat("yyyy-MM-dd");
    public static final DateFormat FORMAT_TIME_ISO_8601 = new SimpleDateFormat("HH:mm:ss");

    private static final String QUOTE = "\"";
    private static final String SEPARATOR = ",";
    private static final String TAG_SEPARATOR = ",";

    private final CsvExportOptions options;
    private DBHelper dbHelper;
    private Context context;
    public static List<Transaction> transactions = Collections.emptyList();
    public static Currency currency;

    public CSVexport(Context context, CsvExportOptions options) {

        this.options = options;
        this.context = context;
        dbHelper = MoneyApplication.getInstance().GetDBHelper();
        currency = CurrencyCache.getCurrencyOrEmpty();
    }

    protected String getExtension() {
        return ".csv";
    }

    protected void writeHeader(BufferedWriter bw) throws IOException {
        if (options.writeUtfBom) {
            byte[] bom = new byte[3];
            bom[0] = (byte) 0xEF;
            bom[1] = (byte) 0xBB;
            bom[2] = (byte) 0xBF;
            bw.write(new String(bom, "UTF-8"));
        }
        if (options.includeHeader) {
            Csv.Writer w = new Csv.Writer(bw).delimiter(options.fieldSeparator);
            for (String h : HEADER) {
                w.value(h);
            }
            w.newLine();
        }
    }

    protected void writeBody(BufferedWriter bw) throws IOException {
        Csv.Writer w = new Csv.Writer(bw).delimiter(options.fieldSeparator);
        try {
            transactions = dbHelper.getTransactionDAO().queryForAll();
            for (Transaction model : transactions) {
                writeLine(w, model);
            }

        } catch (SQLException ex) {
        } finally {
            w.close();
        }
    }

    private void writeLine(Csv.Writer w, Transaction model) {
        String payee = "";
        String category = model.getCategory() != null ? model.getCategory().getTitle() : "";
        writeLine(w, model.getType().toString(), model.getDateAdded(), model.getAccountName(), model.getAccount().getType(), model.getFormatedAmount(),category , model.getNotes(), model.getLocation(), payee);
    }

    private void writeLine(Csv.Writer w, String type, Date dt, String account,
                           int accountType, String amount,
                           String category, String note,
                           String location, String payee) {
        w.value(type);
        if (dt != null) {
            w.value(FORMAT_DATE_ISO_8601.format(dt));
            w.value(FORMAT_TIME_ISO_8601.format(dt));
        } else {
            w.value("~");
            w.value("");
        }
        w.value(account);
        w.value(String.valueOf(accountType));
        w.value(amount);
        w.value(currency.getName());
        w.value("");
        w.value("");
        w.value(category != null ? category : "");
        w.value(location != null ? location : "");
        w.value(note);
        w.value(payee != null ? payee : "");
        w.newLine();
    }



    private void writeCsv(BufferedWriter writer,String payee) throws IOException {
        // "type,date,time,amount,currency,category,note,location,payee,accountName,accountType"
        final StringBuilder outputLine = new StringBuilder();
        List<Transaction> transactions = null;
        for(Transaction transaction: transactions){
            final DateTime dateTime = new DateTime(transaction.getDateAdded());
            outputLine.setLength(0);
            outputLine.append(QUOTE).append(transaction.getType()).append(QUOTE);
            outputLine.append(SEPARATOR).append(QUOTE).append(FORMAT_DATE_ISO_8601.format(dateTime)).append(QUOTE);
            outputLine.append(SEPARATOR).append(QUOTE).append(FORMAT_TIME_ISO_8601.format(dateTime)).append(QUOTE);
            outputLine.append(SEPARATOR).append(QUOTE).append(transaction.getFormatedAmount()).append(QUOTE);
            outputLine.append(SEPARATOR).append(QUOTE).append(currency.getName()).append(QUOTE);
            outputLine.append(SEPARATOR).append(QUOTE).append(transaction.getCategory()!= null ? transaction.getCategory().getTitle() : "").append(QUOTE);
            outputLine.append(SEPARATOR).append(QUOTE).append(transaction.getNotes()).append(QUOTE);
            outputLine.append(SEPARATOR).append(QUOTE).append(transaction.getLocation()!= null ? transaction.getLocation() : "").append(QUOTE);
            outputLine.append(SEPARATOR).append(QUOTE).append(transaction.getAccountName()).append(QUOTE);
            outputLine.append(SEPARATOR).append(QUOTE).append(String.valueOf(transaction.getAccount().getType())).append(QUOTE);
            outputLine.append(SEPARATOR).append(QUOTE).append(payee != null ? payee : "").append(QUOTE);
            writer.write(outputLine.toString());
            writer.newLine();

        }
    }
}
