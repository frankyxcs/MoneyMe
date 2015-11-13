package com.devmoroz.moneyme.dao;

import com.devmoroz.moneyme.models.Currency;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;


public class CurrencyDAO extends BaseDaoImpl<Currency, Integer> {

    public CurrencyDAO(ConnectionSource connectionSource,
                     Class<Currency> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }
}
