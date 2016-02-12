package com.devmoroz.moneyme.helpers;

import android.content.Context;

import com.devmoroz.moneyme.R;
import com.devmoroz.moneyme.models.Category;
import com.devmoroz.moneyme.models.Currency;
import com.devmoroz.moneyme.utils.CurrencyCache;
import com.devmoroz.moneyme.utils.CustomColorTemplate;

import java.sql.SQLException;

public final class DBDefaults {

    private final Context context;
    private final DBHelper dbHelper;

    public DBDefaults(Context context, DBHelper dbHelper) {
        this.context = context;
        this.dbHelper = dbHelper;
    }

    public void addDefaults() {
        addCurrency();
        addCategories();
    }

    private void addCurrency() {
        Currency c = new Currency("USD","United States dollar","$");
        try {
            dbHelper.getCurrencyDAO().create(c);
            CurrencyCache.initialize(dbHelper);
        }catch (SQLException ex){

        }
    }

    private void addCategories() {
        String[] titles = context.getResources().getStringArray(R.array.outcome_categories);
        int[] colors = CustomColorTemplate.PIECHART_COLORS;
        int order = 0;
        try {
            for(String title : titles){
                Category category = new Category(colors[order % colors.length],title,order);
                dbHelper.getCategoryDAO().create(category);
                order++;
            }
        }catch(SQLException ex){

        }
    }
}
