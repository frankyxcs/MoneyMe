package com.devmoroz.moneyme;


import android.app.Application;
import android.content.Context;

import com.devmoroz.moneyme.helpers.DBHelper;
import com.devmoroz.moneyme.helpers.DBHelperFactory;
import com.devmoroz.moneyme.models.CommonInOut;
import com.devmoroz.moneyme.models.Goal;
import com.devmoroz.moneyme.models.Income;
import com.devmoroz.moneyme.models.MyCurrency;
import com.devmoroz.moneyme.models.Outcome;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MoneyApplication extends Application {

    public final static MyCurrency[] MyCurrencyAll = {
            new MyCurrency("$", "USD"),
            new MyCurrency("€", "EUR"),
            new MyCurrency("₴", "UAH"),
            new MyCurrency("\u20BD", "RUB"),
            new MyCurrency("£", "GBP")
    };

    private static MoneyApplication wInstance;

    public static List<Goal> goals;
    public static List<Income> incomes;
    public static List<Outcome> outcomes;
    public static ArrayList<CommonInOut> inout;

    private DBHelper dbHelper;

    public static MoneyApplication getInstance() {
        return wInstance;
    }

    public static Context getAppContext() {
        return wInstance.getApplicationContext();
    }

    public void GetCommonData() {
        try {
            incomes = dbHelper.getIncomeDAO().queryForAll();
            outcomes = dbHelper.getOutcomeDAO().queryForAll();
            inout = new ArrayList<>();

            if (incomes != null) {
                for (Income in : incomes) {
                    inout.add(new CommonInOut(1, in.getId(), in.getAmount(), in.getNotes(), in.getDateOfReceipt(), in.getCurrency()));
                }
            }
            if (outcomes != null) {
                for (Outcome out : outcomes) {
                    inout.add(new CommonInOut(2, out.getId(), out.getAmount(), out.getNotes(), out.getDateOfSpending(), out.getCurrency()));
                }
            }
        } catch (SQLException ex) {

        }
    }

    public DBHelper GetDBHelper() {
        return dbHelper;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        wInstance = this;
        DBHelperFactory.setHelper(getApplicationContext());
        dbHelper = DBHelperFactory.getHelper();
        GetCommonData();
    }

    @Override
    public void onTerminate() {
        DBHelperFactory.releaseHelper();
        super.onTerminate();
    }
}
