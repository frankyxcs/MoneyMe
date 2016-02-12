package com.devmoroz.moneyme.utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.devmoroz.moneyme.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Preferences {

    public static final String DROPBOX_AUTH_TOKEN = "dropbox_auth_token";
    public static final String DROPBOX_AUTHORIZE = "dropbox_authorize";
    public static final String DROPBOX_REMOTE_FILE = "DROPBOX_REMOTE_FILE";
    public static final String DROPBOX_LAST_SYNC_DATE = "DROPBOX_LAST_SYNC_DATE";

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

    public static String getDropboxFilePath(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(DROPBOX_REMOTE_FILE, null);
    }

    public static void storeDropboxLastSyncDate(Context context, Date date) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit()
                .putString(DROPBOX_LAST_SYNC_DATE, new SimpleDateFormat(Constants.SYNC_DATE_FORMAT).format(date))
                .apply();
    }

    public static String getDropboxLastSyncDate(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(DROPBOX_LAST_SYNC_DATE, null);
    }

    public static void storeDropboxFilePath(Context context, String path) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit()
                .putString(DROPBOX_REMOTE_FILE, path)
                .apply();
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
