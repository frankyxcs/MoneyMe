package com.devmoroz.moneyme;


import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;

import com.devmoroz.moneyme.eventBus.AppInitCompletedEvent;
import com.devmoroz.moneyme.eventBus.BusProvider;
import com.devmoroz.moneyme.eventBus.GoalsChangeEvent;
import com.devmoroz.moneyme.eventBus.WalletChangeEvent;
import com.devmoroz.moneyme.helpers.DBHelper;
import com.devmoroz.moneyme.helpers.DBHelperFactory;
import com.devmoroz.moneyme.logging.L;
import com.devmoroz.moneyme.models.Account;
import com.devmoroz.moneyme.models.CommonInOut;
import com.devmoroz.moneyme.models.Goal;
import com.devmoroz.moneyme.models.Income;
import com.devmoroz.moneyme.models.Outcome;
import com.devmoroz.moneyme.utils.CurrencyCache;
import com.devmoroz.moneyme.utils.Preferences;
import com.squareup.otto.Subscribe;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MoneyApplication extends Application {

    private static MoneyApplication wInstance;
    private static boolean mIsInitialized;

    public static boolean isInitialized() {
        return mIsInitialized;
    }

    public static List<Goal> goals = Collections.emptyList();
    public static List<Income> incomes =Collections.emptyList();
    public static List<Outcome> outcomes =Collections.emptyList();
    public static ArrayList<CommonInOut> inout = new ArrayList<>(0);
    public static List<Account> accounts = Collections.emptyList();

    private DBHelper dbHelper;

    public static MoneyApplication getInstance() {
        return wInstance;
    }

    public static Context getAppContext() {
        return wInstance.getApplicationContext();
    }

    public void GetCommonData() {
        try {
            int period = Preferences.getHistoryPeriod(this);
            int monthStart = Preferences.getMonthStart(this);
            accounts = dbHelper.getAccountDAO().queryForAll();
            incomes = dbHelper.getIncomeDAO().queryForPeriod(period,monthStart);
            outcomes = dbHelper.getOutcomeDAO().queryForPeriod(period, monthStart);

            inout = new ArrayList<>();

            if (incomes != null) {
                for (Income in : incomes) {
                    inout.add(new CommonInOut(1, in.getId(), in.getAmount(), null, in.getDateOfReceipt(), in.getAccountName(), null, in.getNotes(), ""));
                }
            }
            if (outcomes != null) {
                for (Outcome out : outcomes) {
                    inout.add(new CommonInOut(2, out.getId(), out.getAmount(), out.getCategory(), out.getDateOfSpending(), out.getAccountName(), out.getPhoto(), out.getNotes(), out.getLocation()));
                }
            }

        } catch (SQLException ex) {
            L.t(this, "Something went wrong.Please,try again.");
        }
    }

    public void GetGoals() {
        try{
            goals = dbHelper.getGoalDAO().queryForInProgress();
        }
        catch (SQLException ex) {
            L.t(this, "Something went wrong.Please,try again.");
        }
    }

    public void notifyApplicationInitializationCompleted(boolean success) {
        mIsInitialized = true;
        BusProvider.getInstance().post(new AppInitCompletedEvent(success));
    }

    protected void initialize() {
        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    GetCommonData();
                    GetGoals();
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean result) {
                notifyApplicationInitializationCompleted(result);
            }
        };
        task.execute();
    }

    public DBHelper GetDBHelper() {
        return dbHelper;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        BusProvider.getInstance().register(this);
        wInstance = this;
        DBHelperFactory.setHelper(getApplicationContext());
        dbHelper = DBHelperFactory.getHelper();
        CurrencyCache.initialize(dbHelper);
        initialize();
    }

    @Override
    public void onTerminate() {
        DBHelperFactory.releaseHelper();
        BusProvider.getInstance().unregister(this);
        super.onTerminate();
    }

    @Subscribe
    public void OnWalletChange(WalletChangeEvent event){
        getInstance().GetCommonData();
    }
    @Subscribe
    public void OnGoalsChange(GoalsChangeEvent event){
        getInstance().GetGoals();
    }

}
