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
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class CSVexport extends Exporter {

    public static final String[] HEADER = "type,date,time,amount,currency,category,note,tags,location,accountName,payeeName".split(",");
    public static final DateFormat FORMAT_DATE_ISO_8601 = new SimpleDateFormat("yyyy-MM-dd");
    public static final DateFormat FORMAT_TIME_ISO_8601 = new SimpleDateFormat("HH:mm:ss");

    private static final String QUOTE = "\"";
    private static final String SEPARATOR = ",";

    private DBHelper dbHelper;
    private Context context;
    public static List<Transaction> transactions = Collections.emptyList();
    public static Currency currency;

    public CSVexport(Context context, OutputStream outputStream) {
        super(context, outputStream);
        this.context = context;
        dbHelper = MoneyApplication.getInstance().GetDBHelper();
        currency = CurrencyCache.getCurrencyOrEmpty();
    }

    @Override
    public void generateExport(OutputStream outputStream) throws Exception {
        transactions = dbHelper.getTransactionDAO().queryForAll();
        final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));

        writeCsv(writer);

        writer.close();
    }

    protected String getExtension() {
        return ".csv";
    }

    private void writeCsv(BufferedWriter writer) throws IOException {
        // "type,date,time,amount,currency,category,note,tags,location,accountName,payeeName"
        final StringBuilder outputLine = new StringBuilder();
        for (Transaction transaction : transactions) {
            outputLine.setLength(0);
            outputLine.append(QUOTE).append(transaction.getType()).append(QUOTE);
            outputLine.append(SEPARATOR).append(QUOTE).append(FORMAT_DATE_ISO_8601.format(transaction.getDateAdded())).append(QUOTE);
            outputLine.append(SEPARATOR).append(QUOTE).append(FORMAT_TIME_ISO_8601.format(transaction.getDateAdded())).append(QUOTE);
            outputLine.append(SEPARATOR).append(QUOTE).append(transaction.getFormatedAmount()).append(QUOTE);
            outputLine.append(SEPARATOR).append(QUOTE).append(currency.getName()).append(QUOTE);
            outputLine.append(SEPARATOR).append(QUOTE).append(transaction.getCategory() != null ? transaction.getCategory().getTitle() : "").append(QUOTE);
            outputLine.append(SEPARATOR).append(QUOTE).append(transaction.getNotes()).append(QUOTE);
            outputLine.append(SEPARATOR).append(QUOTE).append(transaction.getTags() != null ? transaction.getTags() : "").append(QUOTE);
            outputLine.append(SEPARATOR).append(QUOTE).append(transaction.getLocation() != null ? transaction.getLocation() : "").append(QUOTE);
            outputLine.append(SEPARATOR).append(QUOTE).append(transaction.getAccountName()).append(QUOTE);
            outputLine.append(SEPARATOR).append(QUOTE).append(transaction.getPayee() != null ? transaction.getPayee().getName() : "").append(QUOTE);
            writer.write(outputLine.toString());
            writer.newLine();
        }
    }
}
