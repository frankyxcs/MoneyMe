package com.devmoroz.moneyme.utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.devmoroz.moneyme.R;

public class Preferences {

    public static final String DROPBOX_AUTH_TOKEN = "dropbox_auth_token";
    public static final String DROPBOX_AUTHORIZE = "dropbox_authorize";

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

    public static void storeDropboxToken(Context context, String token) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit()
                .putString(DROPBOX_AUTH_TOKEN, token)
                .putBoolean(DROPBOX_AUTHORIZE, true)
                .apply();
    }

    public static String getDropboxToken(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(DROPBOX_AUTH_TOKEN, null);
    }

    public static String getNotificationTime(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(context.getString(R.string.pref_notify_time), null);
    }

    public static void removeDropboxKeys(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit()
                .remove(DROPBOX_AUTH_TOKEN)
                .remove(DROPBOX_AUTHORIZE)
                .apply();
    }

    public static boolean isDropboxAuthorized(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(DROPBOX_AUTHORIZE, false);
    }

}
