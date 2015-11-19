package com.devmoroz.moneyme;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.devmoroz.moneyme.utils.AppCompatPreferenceActivity;

import java.util.List;

public class SettingsActivity  extends AppCompatPreferenceActivity {

    @Override
    public void onBuildHeaders(List<Header> target) {
        //loadHeadersFromResource(R.xml.settings, target);

        setContentView(R.layout.settings_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarSettings);
        setSupportActionBar(toolbar);

        ActionBar bar = getSupportActionBar();

        bar.setHomeButtonEnabled(true);
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setDisplayShowTitleEnabled(true);
        bar.setTitle(R.string.option_settings);
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return SettingsFragment.class.getName().equals(fragmentName);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class SettingsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            getActivity().setTheme(R.style.AppDefault);

            if (getArguments() != null) {
                String page = getArguments().getString("page");
                if (page != null)
                    switch (page) {
                        case "page1":
                            //addPreferencesFromResource(R.xml.settings_page1);
                            break;
                        case "page2":
                           // addPreferencesFromResource(R.xml.settings_page2);
                            break;

                    }
            }
        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View layout = inflater.inflate(R.layout.settings_activity, container, false);
            if (layout != null) {
                AppCompatPreferenceActivity activity = (AppCompatPreferenceActivity) getActivity();
                Toolbar toolbar = (Toolbar) layout.findViewById(R.id.toolbarSettings);
                activity.setSupportActionBar(toolbar);

                ActionBar bar = activity.getSupportActionBar();
                bar.setHomeButtonEnabled(true);
                bar.setDisplayHomeAsUpEnabled(true);
                bar.setDisplayShowTitleEnabled(true);
                bar.setTitle(getPreferenceScreen().getTitle());
            }
            return layout;
        }

        @Override
        public void onResume() {
            super.onResume();

            if (getView() != null) {
                View frame = (View) getView().getParent();
                if (frame != null)
                    frame.setPadding(0, 0, 0, 0);
            }
        }
    }
}
