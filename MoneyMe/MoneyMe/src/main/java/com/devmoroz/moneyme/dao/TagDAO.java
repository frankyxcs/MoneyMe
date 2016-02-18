package com.devmoroz.moneyme.dao;

import com.devmoroz.moneyme.models.Tag;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;


public class TagDAO extends BaseDaoImpl<Tag, UUID> {

    public TagDAO(ConnectionSource connectionSource,
                    Class<Tag> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    public List<Tag> getSortedTags() throws SQLException{
        QueryBuilder<Tag,UUID> queryBuilder = queryBuilder();
        queryBuilder.orderBy("title",true);
        PreparedQuery<Tag> preparedQuery = queryBuilder.prepare();
        List<Tag> result = query(preparedQuery);
        return result;
    }

    public boolean createIfNotExist(Tag tag) throws SQLException{
        QueryBuilder<Tag,UUID> queryBuilder = queryBuilder();
        queryBuilder.where().eq("title", tag.getTitle());
        PreparedQuery<Tag> preparedQuery = queryBuilder.prepare();
        if(query(preparedQuery).isEmpty()){
            create(tag);
            return true;
        }
        return false;
    }

}
