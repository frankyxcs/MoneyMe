package com.devmoroz.moneyme.dao;

import com.devmoroz.moneyme.models.Category;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

import java.util.List;
import java.util.UUID;


public class CategoryDAO extends BaseDaoImpl<Category, UUID> {

    public CategoryDAO(ConnectionSource connectionSource,
                      Class<Category> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    public List<Category> getSortedCategories() throws SQLException{
        QueryBuilder<Category,UUID> queryBuilder = queryBuilder();
        queryBuilder.orderBy("order",true);
        PreparedQuery<Category> preparedQuery = queryBuilder.prepare();
        List<Category> result = query(preparedQuery);
        return result;
    }
}
