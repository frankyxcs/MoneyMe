package com.devmoroz.moneyme.dao;

import com.devmoroz.moneyme.models.Outcome;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.Calendar;
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

    public List<Outcome> queryOutcomesForCategory(String category) throws SQLException{
        QueryBuilder<Outcome,Integer> queryBuilder = queryBuilder();
        queryBuilder.where().eq("category",category);
        PreparedQuery<Outcome> preparedQuery = queryBuilder.prepare();
        List<Outcome> result = query(preparedQuery);

        return result;
    }

    public List<Outcome> queryForCurrentMonth(int monthStart) throws SQLException{
        QueryBuilder<Outcome,Integer> queryBuilder = queryBuilder();

        Calendar dateStart = Calendar.getInstance();
        dateStart.set(Calendar.DAY_OF_MONTH, monthStart);

        Calendar dateEnd = Calendar.getInstance();
        dateEnd.set(Calendar.DAY_OF_MONTH, monthStart);
        dateEnd.add(Calendar.MONTH, 1);

        queryBuilder.where().between("dateOfSpending", dateStart.getTime(), dateEnd.getTime());
        PreparedQuery<Outcome> preparedQuery = queryBuilder.prepare();
        List<Outcome> result = query(preparedQuery);

        return result;
    }

    public List<Outcome> queryForPeriod(int period, int monthStart) throws SQLException{
        QueryBuilder<Outcome,Integer> queryBuilder = queryBuilder();

        Calendar currentDate = Calendar.getInstance();

        Calendar dateStart = Calendar.getInstance();
        Calendar dateEnd = Calendar.getInstance();

        if(currentDate.get(Calendar.DAY_OF_MONTH) == monthStart){
            dateStart.add(Calendar.DAY_OF_MONTH, -1);
        }
        else {
            dateStart.set(Calendar.DAY_OF_MONTH, monthStart);
            dateStart.add(Calendar.DAY_OF_MONTH, -1);
        }
        dateEnd.add(Calendar.DAY_OF_MONTH, 1);

        switch (period) {
            case 1:
                if (currentDate.get(Calendar.DAY_OF_MONTH) < monthStart) {
                    dateStart.add(Calendar.MONTH, -1);
                }
                break;
            case 2:
                if (currentDate.get(Calendar.DAY_OF_MONTH) < monthStart) {
                    dateStart.add(Calendar.MONTH, -2);
                }
                else {
                    dateStart.add(Calendar.MONTH, -1);
                }
                break;
            case 3:
                if (currentDate.get(Calendar.DAY_OF_MONTH) < monthStart) {
                    dateStart.add(Calendar.MONTH, -3);
                }
                else {
                    dateStart.add(Calendar.MONTH, -2);
                }
                break;
            case 6:
                if (currentDate.get(Calendar.DAY_OF_MONTH) < monthStart) {
                    dateStart.add(Calendar.MONTH, -6);
                }
                else {
                    dateStart.add(Calendar.MONTH, -5);
                }
                break;
            case 12:
                if (currentDate.get(Calendar.DAY_OF_MONTH) < monthStart) {
                    dateStart.add(Calendar.MONTH, -12);
                }
                else {
                    dateStart.add(Calendar.MONTH, -11);
                }
                break;
            case 0:
                return queryForAll();
        }

        queryBuilder.where().between("dateOfSpending", dateStart.getTime(), dateEnd.getTime());
        PreparedQuery<Outcome> preparedQuery = queryBuilder.prepare();
        List<Outcome> result = query(preparedQuery);

        return result;
    }
}
