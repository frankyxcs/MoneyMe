package com.devmoroz.moneyme.helpers;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;

public class DBHelperFactory {

    private static DBHelper databaseHelper;

    public static DBHelper getHelper(){
        return databaseHelper;
    }
    public static void setHelper(Context context){
        databaseHelper = OpenHelperManager.getHelper(context, DBHelper.class);
    }
    public static void releaseHelper(){
        OpenHelperManager.releaseHelper();
        databaseHelper = null;
    }
}
