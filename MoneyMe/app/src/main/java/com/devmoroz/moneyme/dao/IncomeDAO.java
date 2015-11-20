package com.devmoroz.moneyme.dao;

import com.devmoroz.moneyme.models.Income;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class IncomeDAO extends BaseDaoImpl<Income, Integer> {

    public IncomeDAO(ConnectionSource connectionSource,
                   Class<Income> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    public List<Income> getIncomesForPeriod(Date dateStart, Date dateEnd) throws SQLException{
        QueryBuilder<Income,Integer> queryBuilder = queryBuilder();
        queryBuilder.where().between("dateOfReceipt", dateStart, dateEnd);
        PreparedQuery<Income> preparedQuery = queryBuilder.prepare();
        List<Income> result = query(preparedQuery);

        return result;
    }
}
