package com.devmoroz.moneyme.utils;


import com.devmoroz.moneyme.dao.CurrencyDAO;
import com.devmoroz.moneyme.helpers.DBHelper;
import com.devmoroz.moneyme.models.Currency;

import java.sql.SQLException;
import java.util.List;

public class CurrencyCache {

    private static final Currency CURRENCY = Currency.EMPTY;

    public static synchronized Currency getCurrencyOrEmpty() {
        Currency c = CURRENCY;
        return !c.isEmpty() ? c : Currency.EMPTY;
    }

    /*public static synchronized Currency getCurrency(DBHelper dbHelper) {
        Currency cachedCurrency = getCurrencyOrEmpty();
        if (cachedCurrency.isEmpty()) {
            cachedCurrency = em.get(Currency.class, currencyId);
            if (cachedCurrency == null) {
                cachedCurrency = Currency.EMPTY;
            }
            CURRENCIES.put(currencyId, cachedCurrency);
        }
        return cachedCurrency;
    }*/

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
}
