package com.devmoroz.moneyme.dao;

import com.devmoroz.moneyme.models.Todo;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.UUID;


public class TodoDAO extends BaseDaoImpl<Todo, UUID> {

    public TodoDAO(ConnectionSource connectionSource,
                      Class<Todo> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    public List<Todo> queryForAllSorted() throws SQLException{
        QueryBuilder<Todo,UUID> queryBuilder = queryBuilder();
        queryBuilder.orderBy("date",false);
        PreparedQuery<Todo> preparedQuery = queryBuilder.prepare();
        List<Todo> result = query(preparedQuery);

        return result;
    }

    public List<Todo> queryForAlarm(long now) throws SQLException{
        QueryBuilder<Todo,UUID> queryBuilder = queryBuilder();
        queryBuilder.where().eq("hasReminder",true).and().gt("date" , new Date(now));
        queryBuilder.orderBy("date", false);
        PreparedQuery<Todo> preparedQuery = queryBuilder.prepare();
        List<Todo> result = query(preparedQuery);

        return result;
    }
}
