package com.devmoroz.moneyme.utils;


import com.devmoroz.moneyme.dao.CurrencyDAO;
import com.devmoroz.moneyme.helpers.DBHelper;
import com.devmoroz.moneyme.models.Currency;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;

public class CurrencyCache {

    private static final Currency CURRENCY = Currency.EMPTY;

    public static synchronized Currency getCurrencyOrEmpty() {
        Currency c = CURRENCY;
        return !c.isEmpty() ? c : Currency.EMPTY;
    }

    public static void initialize(DBHelper dbHelper) {
        try {
            List<Currency> currencies;
            CurrencyDAO cDAO = dbHelper.getCurrencyDAO();
            currencies = cDAO.queryForAll();
            if (currencies.size() != 0) {
                Currency currentCurrency = currencies.get(0);
                CURRENCY.setId(currentCurrency.getId());
                CURRENCY.setTitle(currentCurrency.getTitle());
                CURRENCY.setName(currentCurrency.getName());
                CURRENCY.setSymbol(currentCurrency.getSymbol());
            }
        } catch (SQLException ex) {

        }

    }

    public static String formatAmountWithSign(double amount) {
        String sign = getCurrencyOrEmpty().getSymbol();

        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(',');
        symbols.setDecimalSeparator('.');

        DecimalFormat decimalFormat = new DecimalFormat("#,###.00 " + sign, symbols);
        decimalFormat.setGroupingSize(3);

        return decimalFormat.format(amount);
    }
}
