package com.devmoroz.moneyme.helpers;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;


import com.devmoroz.moneyme.R;
import com.devmoroz.moneyme.logging.L;
import com.devmoroz.moneyme.models.Currency;
import com.devmoroz.moneyme.utils.csv.Csv;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CurrencyHelper {

    private final Context context;
    private final List<List<String>> currencies;
    private int selectedCurrency = 0;

    public CurrencyHelper(Context context) {
        this.context = context;
        this.currencies = readCurrenciesFromAsset();
    }

    public void show() {
        String[] items = createItemsList(currencies);
        new AlertDialog.Builder(context)
                .setTitle(R.string.currencies)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        addSelectedCurrency(selectedCurrency);
                        dialogInterface.dismiss();
                    }
                })
                .setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        selectedCurrency = i;
                    }
                })
                .show();
    }

    public void addSelectedCurrency(int selectedCurrency) {
        if (selectedCurrency > 0 && selectedCurrency <= currencies.size()) {
            List<String> c = currencies.get(selectedCurrency-1);
            addSelectedCurrency(c);
        }
    }

    private void addSelectedCurrency(List<String> list) {
        Currency c = new Currency(list.get(0),list.get(1),list.get(2));

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
        String[] items = new String[size+1];
        items[0] = "";
        for (int i=0; i<size; i++) {
            List<String> c = currencies.get(i);
            items[i+1] = c.get(0)+" ("+c.get(1)+")";
        }
        return items;
    }
}
