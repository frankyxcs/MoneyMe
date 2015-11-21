package com.devmoroz.moneyme.dao;

import com.devmoroz.moneyme.models.Income;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.Calendar;
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

    public List<Income> queryForCurrentMonth() throws SQLException{
        QueryBuilder<Income,Integer> queryBuilder = queryBuilder();

        Calendar dateStart = Calendar.getInstance();
        dateStart.set(Calendar.DAY_OF_MONTH, 1);

        Calendar dateEnd = Calendar.getInstance();
        dateEnd.set(Calendar.DAY_OF_MONTH, 1);
        dateEnd.add(Calendar.MONTH, 1);

        queryBuilder.where().between("dateOfReceipt", dateStart.getTime(), dateEnd.getTime());
        PreparedQuery<Income> preparedQuery = queryBuilder.prepare();
        List<Income> result = query(preparedQuery);

        return result;
    }
}
