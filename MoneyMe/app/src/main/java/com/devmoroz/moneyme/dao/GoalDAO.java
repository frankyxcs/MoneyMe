package com.devmoroz.moneyme.dao;


import com.devmoroz.moneyme.models.Goal;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

public class GoalDAO extends BaseDaoImpl<Goal, Integer> {

    public GoalDAO(ConnectionSource connectionSource,
                   Class<Goal> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

}
