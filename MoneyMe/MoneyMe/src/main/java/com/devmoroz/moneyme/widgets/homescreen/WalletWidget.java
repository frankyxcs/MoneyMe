package com.devmoroz.moneyme.widgets.homescreen;


import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.widget.RemoteViews;

import com.devmoroz.moneyme.R;
import com.devmoroz.moneyme.utils.Constants;
import com.devmoroz.moneyme.utils.CustomColorTemplate;
import com.devmoroz.moneyme.widgets.homescreen.WidgetProvider;

public class WalletWidget extends WidgetProvider {

    @Override
    protected RemoteViews getRemoteViews(Context mContext, int widgetId, boolean isExtended,
                                         SparseArray<PendingIntent> pendingIntentsMap, WalletBalance balance) {
        RemoteViews views;

        int textColor = mContext.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_MULTI_PROCESS)
                .getInt(Constants.PREF_WIDGET_TC + String.valueOf(widgetId),
                        CustomColorTemplate.WHITE_TEXT);

        int backgroundColor = mContext.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_MULTI_PROCESS)
                .getInt(Constants.PREF_WIDGET_BC + String.valueOf(widgetId),
                        CustomColorTemplate.TRANSPARENT);

        views = new RemoteViews(mContext.getPackageName(), R.layout.widget_toolbar);
        views.setInt(R.id.toolbar_widget, "setBackgroundColor", backgroundColor);
        views.setTextColor(R.id.widget_total_balance, textColor);
        views.setOnClickPendingIntent(R.id.widget_note, pendingIntentsMap.get(R.id.widget_note));
        views.setOnClickPendingIntent(R.id.widget_add, pendingIntentsMap.get(R.id.widget_add));
        views.setOnClickPendingIntent(R.id.widget_minus, pendingIntentsMap.get(R.id.widget_minus));
        views.setTextViewText(R.id.widget_total_balance, balance.TotalBalance);

        if(isExtended){
            views.setTextViewText(R.id.widget_accounts_name, balance.AccountsNames);
            views.setTextViewText(R.id.widget_accounts_balance, balance.AccountsBalance);
            views.setTextColor(R.id.widget_accounts_name, textColor);
            views.setTextColor(R.id.widget_accounts_balance, textColor);
        }
        else{
            views.setViewVisibility(R.id.widget_accounts_balance_container, View.GONE);
        }

        return views;
    }

}
