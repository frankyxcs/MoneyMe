package com.devmoroz.moneyme.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.devmoroz.moneyme.dao.GoalDAO;
import com.devmoroz.moneyme.models.Goal;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;


public class DBHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME ="moneyme.db";

    private static final int DATABASE_VERSION = 1;

    private GoalDAO goalDao = null;

    public DBHelper(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db,ConnectionSource connectionSource) {
        try
        {
            TableUtils.createTable(connectionSource, Goal.class);
        }
        catch (SQLException e){
            Log.e("","error creating DB " + DATABASE_NAME);
            throw new RuntimeException(e);
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db,ConnectionSource connectionSource, int oldVersion, int newVersion) {

    }

    @Override
    public void close(){
        super.close();
        goalDao = null;
    }

    public GoalDAO getGoalDAO() throws SQLException{
        if(goalDao == null){
            goalDao = new GoalDAO(getConnectionSource(), Goal.class);
        }
        return goalDao;
    }
}
