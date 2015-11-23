package com.devmoroz.moneyme.dao;


import com.devmoroz.moneyme.models.Goal;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class GoalDAO extends BaseDaoImpl<Goal, Integer> {

    public GoalDAO(ConnectionSource connectionSource,
                   Class<Goal> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    public List<Goal> queryForInProgress() throws SQLException{
        QueryBuilder<Goal,Integer> queryBuilder = queryBuilder();
        Calendar currentDate = Calendar.getInstance();
        queryBuilder.where().gt("deadlineDate", currentDate.getTime()).and().eq("achieved",false);
        PreparedQuery<Goal> preparedQuery = queryBuilder.prepare();
        List<Goal> result = query(preparedQuery);
        return result;
    }

    public List<Goal> queryForAchived() throws SQLException{
        QueryBuilder<Goal,Integer> queryBuilder = queryBuilder();
        queryBuilder.where().eq("achieved",true);
        PreparedQuery<Goal> preparedQuery = queryBuilder.prepare();
        List<Goal> result = query(preparedQuery);
        return result;
    }
}
