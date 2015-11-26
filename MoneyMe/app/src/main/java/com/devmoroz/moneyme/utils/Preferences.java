package com.devmoroz.moneyme.utils;


import android.content.Context;
import android.preference.PreferenceManager;

import com.devmoroz.moneyme.R;

public class Preferences {

    public static void reset(Context context) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .clear()
                .apply();
    }

    public static boolean isSortByDesc(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(context.getString(R.string.pref_history_order),
                        context.getString(R.string.pref_history_order_value_desc))
                .equals(context.getString(R.string.pref_history_order_value_desc));
    }

    public static int getHistoryPeriod(Context context) {
        String historyPeriodString = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(context.getString(R.string.pref_history_period), null);
        int period = historyPeriodString == null ? 0 : Integer.parseInt(historyPeriodString);

        return period;
    }

    public static int getMonthStart(Context context) {
        int monthStart = PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(context.getString(R.string.pref_month_start), 1);

        return monthStart;
    }
}
