package com.devmoroz.moneyme;


import android.app.Application;
import android.content.Context;

import com.devmoroz.moneyme.helpers.DBHelper;
import com.devmoroz.moneyme.helpers.DBHelperFactory;
import com.devmoroz.moneyme.models.Goal;
import com.devmoroz.moneyme.models.Income;
import com.devmoroz.moneyme.models.Outcome;

import java.sql.SQLException;
import java.util.List;

public class MoneyApplication extends Application{

    private static MoneyApplication wInstance;

    public static List<Goal> goals;
    public static List<Income> incomes;
    public static List<Outcome> outcomes;

    private DBHelper dbHelper;

    public static MoneyApplication getInstance() {
        return wInstance;
    }

    public static Context getAppContext() {
        return wInstance.getApplicationContext();
    }

    public void GetAvailableData(){
        try{
            goals = dbHelper.getGoalDAO().queryForAll();
            incomes = dbHelper.getIncomeDAO().queryForAll();
            outcomes = dbHelper.getOutcomeDAO().queryForAll();
        }catch (SQLException ex){

        }
    }

    public DBHelper GetDBHelper(){
        return dbHelper;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        wInstance = this;
        DBHelperFactory.setHelper(getApplicationContext());
        dbHelper = DBHelperFactory.getHelper();
        GetAvailableData();
    }

    @Override
    public void onTerminate() {
        DBHelperFactory.releaseHelper();
        super.onTerminate();
    }
}
