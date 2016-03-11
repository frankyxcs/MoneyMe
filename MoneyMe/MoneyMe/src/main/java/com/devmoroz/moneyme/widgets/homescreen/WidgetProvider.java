package com.devmoroz.moneyme.widgets.homescreen;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.SparseArray;
import android.widget.RemoteViews;

import com.devmoroz.moneyme.AddIncomeActivity;
import com.devmoroz.moneyme.AddOutcomeActivity;
import com.devmoroz.moneyme.MainActivity;
import com.devmoroz.moneyme.MoneyApplication;
import com.devmoroz.moneyme.NotesActivity;
import com.devmoroz.moneyme.R;
import com.devmoroz.moneyme.TodoActivity;
import com.devmoroz.moneyme.helpers.DBHelper;
import com.devmoroz.moneyme.helpers.DBHelperFactory;
import com.devmoroz.moneyme.models.Account;
import com.devmoroz.moneyme.models.Currency;
import com.devmoroz.moneyme.utils.Constants;
import com.devmoroz.moneyme.utils.CurrencyCache;
import com.devmoroz.moneyme.utils.FormatUtils;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;


public abstract class WidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // Get all ids
        ComponentName thisWidget = new ComponentName(context, getClass());
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        for (int appWidgetId : allWidgetIds) {
            // Get the layout for and attach an on-click listener to views
            setLayout(context, appWidgetManager, appWidgetId);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }


    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId,
                                          Bundle newOptions) {

        setLayout(context, appWidgetManager, appWidgetId);
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setLayout(Context context, AppWidgetManager appWidgetManager, int widgetId) {

        String balanceText = loadBalance(context);
        // Create an Intent to launch AddOutcomeActivity
        Intent intentOutcome = new Intent(context, MainActivity.class);
        intentOutcome.putExtra(Constants.INTENT_WIDGET, widgetId);
        intentOutcome.setAction(Constants.ACTION_LAUNCH_OUTCOME);
        PendingIntent pendingIntentOutcome = PendingIntent.getActivity(context, widgetId, intentOutcome,
                Intent.FLAG_ACTIVITY_NEW_TASK);

        // Create an Intent to launch AddIncomeActivity
        Intent intentIncome = new Intent(context, MainActivity.class);
        intentIncome.putExtra(Constants.INTENT_WIDGET, widgetId);
        intentIncome.setAction(Constants.ACTION_LAUNCH_INCOME);
        PendingIntent pendingIntentIncome = PendingIntent.getActivity(context, widgetId, intentIncome,
                Intent.FLAG_ACTIVITY_NEW_TASK);

        // Create an Intent to launch TodoActivity
        Intent intentTodo = new Intent(context, NotesActivity.class);
        intentTodo.putExtra(Constants.INTENT_WIDGET, widgetId);
        intentTodo.setAction(Constants.ACTION_LAUNCH_TODO);
        PendingIntent pendingIntentTodo = PendingIntent.getActivity(context, widgetId, intentTodo,
                Intent.FLAG_ACTIVITY_NEW_TASK);

        // Check various dimensions aspect of widget to choose between layouts
        boolean isSmall = false;
        boolean isSingleLine = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            Bundle options = appWidgetManager.getAppWidgetOptions(widgetId);
            // Width check
            isSmall = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH) < 110;
            // Height check
            isSingleLine = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT) < 110;
        }

        // Creation of a map to associate PendingIntent(s) to views
        SparseArray<PendingIntent> map = new SparseArray<>();
        map.put(R.id.widget_note, pendingIntentTodo);
        map.put(R.id.widget_add, pendingIntentIncome);
        map.put(R.id.widget_minus, pendingIntentOutcome);

        RemoteViews views = getRemoteViews(context, widgetId, isSmall, isSingleLine, map, balanceText);

        // Tell the AppWidgetManager to perform an update on the current app
        // widget
        appWidgetManager.updateAppWidget(widgetId, views);
    }

    private String loadBalance(Context context) {
        DBHelperFactory.setHelper(context);
        DBHelper dbHelper = DBHelperFactory.getHelper();
        CurrencyCache.initialize(dbHelper);
        double totalBalance = 0f;
        List<Account> accounts = Collections.emptyList();
        try {
            accounts = dbHelper.getAccountDAO().queryForAll();
        } catch (SQLException ex) {

        }
        totalBalance = 0f;
        for (Account acc : accounts) {
            totalBalance += acc.getBalance();
        }
        Currency currency = CurrencyCache.getCurrencyOrEmpty();
        String text = context.getString(R.string.balance);
        String formattedBalanceText = FormatUtils.attachAmountToTextWithoutBrackets(text, currency, totalBalance, false);

        DBHelperFactory.releaseHelper();

        return formattedBalanceText;
    }

    public static void updateConfiguration(Context mContext, int mAppWidgetId, int textColor, int backgroundColor,
                                           boolean isExtended) {
        SharedPreferences.Editor edit = mContext.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_MULTI_PROCESS).edit();
        edit.putInt(Constants.PREF_WIDGET_TC + String.valueOf(mAppWidgetId), textColor);
        edit.putInt(Constants.PREF_WIDGET_BC + String.valueOf(mAppWidgetId), backgroundColor);
        edit.putBoolean(Constants.PREF_WIDGET_EX + String.valueOf(mAppWidgetId), isExtended);

        edit.apply();
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        for (int mAppWidgetId : appWidgetIds) {
            SharedPreferences.Editor edit = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_MULTI_PROCESS).edit();
            edit.remove(Constants.PREF_WIDGET_TC + String.valueOf(mAppWidgetId))
                    .remove(Constants.PREF_WIDGET_BC + String.valueOf(mAppWidgetId))
                    .remove(Constants.PREF_WIDGET_EX + String.valueOf(mAppWidgetId)).commit();
        }
        super.onDeleted(context, appWidgetIds);
    }

    abstract protected RemoteViews getRemoteViews(Context context, int widgetId, boolean isSmall, boolean isSingleLine, SparseArray<PendingIntent> pendingIntentsMap, String balance);
}
