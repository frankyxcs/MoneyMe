package com.devmoroz.moneyme.dao;

import com.devmoroz.moneyme.models.Outcome;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class OutcomeDAO extends BaseDaoImpl<Outcome, Integer> {

    public OutcomeDAO(ConnectionSource connectionSource,
                      Class<Outcome> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    public List<Outcome> getOutcomesForPeriod(Date dateStart, Date dateEnd) throws SQLException{
        QueryBuilder<Outcome,Integer> queryBuilder = queryBuilder();
        queryBuilder.where().between("dateOfSpending", dateStart, dateEnd);
        PreparedQuery<Outcome> preparedQuery = queryBuilder.prepare();
        List<Outcome> result = query(preparedQuery);

        return result;
    }
}
