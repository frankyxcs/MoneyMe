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
}
