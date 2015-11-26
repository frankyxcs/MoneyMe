package com.devmoroz.moneyme.fragments;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.devmoroz.moneyme.R;
import com.devmoroz.moneyme.utils.preference.NumberPickerPreference;
import com.devmoroz.moneyme.utils.preference.NumberPickerPreferenceDialog;

public class SettingsFragment extends PreferenceFragmentCompat {

    protected SharedPreferences.OnSharedPreferenceChangeListener mListener;
    private static final String DIALOG_FRAGMENT_TAG = "android.support.v7.preference.PreferenceFragment.DIALOG";

    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        if (preference instanceof NumberPickerPreference) {
            // Inherit the same behaviour as parent
            if (getFragmentManager().findFragmentByTag(DIALOG_FRAGMENT_TAG) != null) {
                return;
            }
            final DialogFragment fragment = NumberPickerPreferenceDialog.newInstance(preference.getKey());
            fragment.setTargetFragment(this, 0);
            fragment.show(getFragmentManager(), DIALOG_FRAGMENT_TAG);
        } else {
            super.onDisplayPreferenceDialog(preference);
        }
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(mListener);
    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(mListener);
        super.onPause();
    }
}
