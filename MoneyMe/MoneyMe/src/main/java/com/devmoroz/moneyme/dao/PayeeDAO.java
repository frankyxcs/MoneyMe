package com.devmoroz.moneyme.dao;


import com.devmoroz.moneyme.models.Payee;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.UUID;

public class PayeeDAO extends BaseDaoImpl<Payee, UUID> {

    public PayeeDAO(ConnectionSource connectionSource,
                     Class<Payee> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

}
