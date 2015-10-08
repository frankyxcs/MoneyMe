package com.devmoroz.moneyme.dao;

import com.devmoroz.moneyme.models.Outcome;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

public class OutcomeDAO extends BaseDaoImpl<Outcome, Integer> {

    public OutcomeDAO(ConnectionSource connectionSource,
                      Class<Outcome> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }
}
