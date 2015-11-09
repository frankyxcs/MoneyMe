package com.devmoroz.moneyme;


import android.app.Application;
import android.content.Context;

import com.devmoroz.moneyme.eventBus.BusProvider;
import com.devmoroz.moneyme.eventBus.WalletChangeEvent;
import com.devmoroz.moneyme.helpers.DBHelper;
import com.devmoroz.moneyme.helpers.DBHelperFactory;
import com.devmoroz.moneyme.models.CommonInOut;
import com.devmoroz.moneyme.models.Currency;
import com.devmoroz.moneyme.models.Goal;
import com.devmoroz.moneyme.models.Income;
import com.devmoroz.moneyme.models.Outcome;
import com.squareup.otto.Subscribe;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MoneyApplication extends Application {

    public static Currency currentCurrency;

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
            List<Currency> currencies;
            incomes = dbHelper.getIncomeDAO().queryForAll();
            outcomes = dbHelper.getOutcomeDAO().queryForAll();
            currencies = dbHelper.getCurrencyDAO().queryForAll();

            inout = new ArrayList<>();

            if (incomes != null) {
                for (Income in : incomes) {
                    inout.add(new CommonInOut(1, in.getId(), in.getAmount(), in.getNotes(),in.getAccount(), in.getDateOfReceipt(), in.getAccount()));
                }
            }
            if (outcomes != null) {
                for (Outcome out : outcomes) {
                    inout.add(new CommonInOut(2, out.getId(), out.getAmount(), out.getNotes(),out.getCategory(), out.getDateOfSpending(), out.getAccount()));
                }
            }

            if(currencies.size() == 0){
                currentCurrency = new Currency();
            }else{
                currentCurrency = currencies.get(0);
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
        BusProvider.getInstance().register(this);
        wInstance = this;
        DBHelperFactory.setHelper(getApplicationContext());
        dbHelper = DBHelperFactory.getHelper();
        GetCommonData();
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


}
