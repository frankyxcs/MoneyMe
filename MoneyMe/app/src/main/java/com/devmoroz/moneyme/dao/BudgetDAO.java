package com.devmoroz.moneyme.dao;


import com.devmoroz.moneyme.models.Budget;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class BudgetDAO extends BaseDaoImpl<Budget, UUID> {

    public BudgetDAO(ConnectionSource connectionSource,
                       Class<Budget> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

}
