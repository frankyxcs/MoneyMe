package com.devmoroz.moneyme;


import android.app.Application;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.devmoroz.moneyme.eventBus.AppInitCompletedEvent;
import com.devmoroz.moneyme.eventBus.BusProvider;
import com.devmoroz.moneyme.eventBus.GoalsChangeEvent;
import com.devmoroz.moneyme.eventBus.WalletChangeEvent;
import com.devmoroz.moneyme.helpers.DBHelper;
import com.devmoroz.moneyme.helpers.DBHelperFactory;
import com.devmoroz.moneyme.logging.L;
import com.devmoroz.moneyme.models.Account;
import com.devmoroz.moneyme.models.Goal;
import com.devmoroz.moneyme.models.Transaction;
import com.devmoroz.moneyme.utils.CurrencyCache;
import com.devmoroz.moneyme.utils.Preferences;
import com.devmoroz.moneyme.widgets.homescreen.WalletWidget;
import com.devmoroz.moneyme.widgets.homescreen.WidgetProvider;
import com.squareup.otto.Subscribe;

import net.danlew.android.joda.JodaTimeAndroid;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class MoneyApplication extends Application {

    private static MoneyApplication wInstance;
    private static boolean mIsInitialized;

    public static boolean isInitialized() {
        return mIsInitialized;
    }

    private static List<Goal> goals = Collections.emptyList();
    private static List<Transaction> transactions = Collections.emptyList();
    private static List<Account> accounts = Collections.emptyList();

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
            transactions = dbHelper.getTransactionDAO().queryAllForPeriod(period, monthStart);
            goals = dbHelper.getGoalDAO().queryForInProgress();
        } catch (SQLException ex) {
            L.t(this, "Something went wrong.Please,try again.");
        }
    }

    public List<Account> getAccounts() {
        try {
            accounts = dbHelper.getAccountDAO().queryForAll();
            return accounts;
        } catch (SQLException ex) {
            return accounts;
        }
    }

    private void updateAppWidgets() {
        Intent intent = new Intent(this, WalletWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

        int[] appWidgetIds = AppWidgetManager.getInstance(this).getAppWidgetIds(new ComponentName(this, WalletWidget.class));
        if (appWidgetIds.length > 0) {
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
            sendBroadcast(intent);
        }
    }

    public List<Transaction> getTransactions() {
        try {
            int period = Preferences.getHistoryPeriod(this);
            int monthStart = Preferences.getMonthStart(this);
            transactions = dbHelper.getTransactionDAO().queryAllForPeriod(period, monthStart);
            return transactions;
        } catch (SQLException ex) {
            return transactions;
        }
    }

    public List<Goal> GetGoals() {
        try {
            goals = dbHelper.getGoalDAO().queryForInProgress();
            return goals;
        } catch (SQLException ex) {
            return goals;
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

    public static DBHelper GetDBHelper() {
        return wInstance.dbHelper;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        BusProvider.getInstance().register(this);
        wInstance = this;
        DBHelperFactory.setHelper(getApplicationContext());
        dbHelper = DBHelperFactory.getHelper();
        CurrencyCache.initialize(dbHelper);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );
        JodaTimeAndroid.init(this);
        initialize();
    }

    @Override
    public void onTerminate() {
        DBHelperFactory.releaseHelper();
        BusProvider.getInstance().unregister(this);
        super.onTerminate();
    }



    @Subscribe
    public void OnWalletChange(WalletChangeEvent event) {
        getInstance().GetCommonData();
        updateAppWidgets();
    }

    @Subscribe
    public void OnGoalsChange(GoalsChangeEvent event) {
        getInstance().GetGoals();
    }

}
