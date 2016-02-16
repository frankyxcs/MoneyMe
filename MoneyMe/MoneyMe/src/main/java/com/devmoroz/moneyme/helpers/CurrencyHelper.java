package com.devmoroz.moneyme.helpers;

import android.content.Context;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.devmoroz.moneyme.R;
import com.devmoroz.moneyme.dao.CurrencyDAO;
import com.devmoroz.moneyme.logging.L;
import com.devmoroz.moneyme.models.Currency;
import com.devmoroz.moneyme.utils.CurrencyCache;
import com.devmoroz.moneyme.utils.csv.Csv;

import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CurrencyHelper {

    private final Context context;
    private DBHelper dbHelper;
    private final List<List<String>> currencies;
    private int selectedCurrency = 0;
    private final Callback mCallback;

    public interface Callback {
        void onSelectClick(String currencyText);
    }

    public CurrencyHelper(Context context, DBHelper dbHelper, Callback callback) {
        this.context = context;
        this.dbHelper = dbHelper;
        this.mCallback = callback;
        this.currencies = readCurrenciesFromAsset();
    }

    public void show() {
        final String[] items = createItemsList(currencies);
        new MaterialDialog.Builder(context)
                .title(R.string.currencies)
                .positiveText(R.string.ok)
                .dividerColorRes(R.color.colorPrimaryDark)
                .titleColorRes(R.color.colorPrimaryDark)
                .positiveColorRes(R.color.colorPrimary)
                .widgetColorRes(R.color.colorPrimary)
                .items(items)
                .itemsCallbackSingleChoice(143, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                        selectedCurrency = i;
                        addSelectedCurrency(selectedCurrency);
                        mCallback.onSelectClick(items[selectedCurrency]);
                        return true;
                    }
                })
                .show();
    }

    public void addSelectedCurrency(int selectedCurrency) {
        if (selectedCurrency <= currencies.size()) {
            List<String> c = currencies.get(selectedCurrency);
            addSelectedCurrency(c);
        }
    }

    private void addSelectedCurrency(List<String> list) {
        Currency c = new Currency(list.get(0), list.get(1), list.get(2));
        try {
            List<Currency> currencies;
            CurrencyDAO cDAO = dbHelper.getCurrencyDAO();
            currencies = cDAO.queryForAll();
            if (currencies.size() != 0) {
                Currency lastC = currencies.get(0);
                c.setId(lastC.getId());
                cDAO.update(c);
                CurrencyCache.initialize(dbHelper);
            } else {
                cDAO.create(c);
                CurrencyCache.initialize(dbHelper);
            }
        } catch (SQLException ex) {
            L.t(context, "Something went wrong.Please,try again.");
        }
    }

    private List<List<String>> readCurrenciesFromAsset() {
        try {
            InputStreamReader r = new InputStreamReader(context.getAssets().open("currencies.csv"), "UTF-8");
            try {
                Csv.Reader csv = new Csv.Reader(r).delimiter(',').ignoreEmptyLines(true);
                List<List<String>> allLines = new ArrayList<List<String>>();
                List<String> line;
                while ((line = csv.readLine()) != null) {
                    if (line.size() == 3) {
                        allLines.add(line);
                    }
                }
                return allLines;
            } finally {
                r.close();
            }
        } catch (IOException e) {
            L.t(context, e.getMessage());
        }
        return Collections.emptyList();
    }

    private String[] createItemsList(List<List<String>> currencies) {
        int size = currencies.size();
        String[] items = new String[size + 1];
        for (int i = 0; i < size; i++) {
            List<String> c = currencies.get(i);
            items[i] = c.get(0) + " (" + c.get(1) + ")";
        }
        return items;
    }
}
