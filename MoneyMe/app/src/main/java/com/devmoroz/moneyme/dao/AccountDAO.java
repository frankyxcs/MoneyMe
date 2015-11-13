package com.devmoroz.moneyme.dao;


import com.devmoroz.moneyme.models.Account;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

public class AccountDAO extends BaseDaoImpl<Account, Integer> {

    public AccountDAO(ConnectionSource connectionSource,
                       Class<Account> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }
}
