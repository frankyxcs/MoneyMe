package com.devmoroz.moneyme.dao;


import com.devmoroz.moneyme.models.Account;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class AccountDAO extends BaseDaoImpl<Account, UUID> {

    public AccountDAO(ConnectionSource connectionSource,
                       Class<Account> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    public List<Account> getAccountsIncludedInTotal() throws SQLException{
        QueryBuilder<Account,UUID> queryBuilder = queryBuilder();
        queryBuilder.where().eq(Account.INCLUDE_IN_TOTAL_FIELD_NAME,true);
        PreparedQuery<Account> preparedQuery = queryBuilder.prepare();
        List<Account> result = query(preparedQuery);
        return result;
    }
}
