package com.devmoroz.moneyme.export;


import android.content.Context;

import com.devmoroz.moneyme.MoneyApplication;
import com.devmoroz.moneyme.helpers.DBHelper;
import com.devmoroz.moneyme.models.Currency;
import com.devmoroz.moneyme.models.ExportImportModel;
import com.devmoroz.moneyme.models.Income;
import com.devmoroz.moneyme.models.Outcome;
import com.devmoroz.moneyme.models.Payee;
import com.devmoroz.moneyme.utils.CurrencyCache;
import com.devmoroz.moneyme.utils.csv.Csv;
import com.devmoroz.moneyme.utils.csv.CsvExportOptions;

import java.io.BufferedWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class CSVexport {

    public static final String[] HEADER = "date,account,accountType,amount,currency,category,location,note,payee".split(",");
    public static final DateFormat FORMAT_DATE_ISO_8601 = new SimpleDateFormat("yyyy-MM-dd");
    public static final DateFormat FORMAT_TIME_ISO_8601 = new SimpleDateFormat("HH:mm:ss");

    private final CsvExportOptions options;
    private DBHelper dbHelper;
    private Context context;
    public static List<Income> incomes = Collections.emptyList();
    public static List<Outcome> outcomes = Collections.emptyList();
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
            incomes = dbHelper.getIncomeDAO().queryForAll();
            outcomes = dbHelper.getOutcomeDAO().queryForAll();
            ArrayList<ExportImportModel> data = ExportImportModel.convertFromEntities(incomes, outcomes);
            if (!data.isEmpty()) {
                for (ExportImportModel model : data) {
                    writeLine(w, model);
                }
            }
        } catch (SQLException ex) {
        } finally {
            w.close();
        }
    }

    private void writeLine(Csv.Writer w, ExportImportModel model) {
        String payee = "";
        writeLine(w, model.getType(), model.getDate(), model.getAccount(), model.getAccountType(), model.getAmount(), model.getCategory(), model.getNotes(), model.getLocation(), payee);
    }

    private void writeLine(Csv.Writer w, int type, Date dt, String account,
                           String accountType, String amount,
                           String category, String note,
                           String location, String payee) {
        if (dt != null) {
            w.value(FORMAT_DATE_ISO_8601.format(dt));
            w.value(FORMAT_TIME_ISO_8601.format(dt));
        } else {
            w.value("~");
            w.value("");
        }
        w.value(account);
        w.value(accountType);
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

}
