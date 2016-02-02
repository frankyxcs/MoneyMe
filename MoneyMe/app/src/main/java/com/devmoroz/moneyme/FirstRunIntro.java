package com.devmoroz.moneyme;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;

import com.devmoroz.moneyme.fragments.Intro.FinishFragment;
import com.github.paolorotolo.appintro.AppIntro;

public class FirstRunIntro extends AppIntro {

    private FinishFragment finishFragment;

    @Override
    public void init(Bundle savedInstanceState) {
        doneButton.setEnabled(false);
        finishFragment = FinishFragment.newInstance(doneButton);
        addSlide(finishFragment);
    }

    @Override
    public void onBackPressed() {

    }

    private void loadMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onSkipPressed() {
        if (pager.isNextPagingEnabled()) {
            pager.setCurrentItem(fragments.size() - 1);
        }
    }

    @Override
    public void onNextPressed() {

    }

    @Override
    public void onDonePressed() {
        if (finishFragment.setUpAccount()){
            SharedPreferences getPrefs = PreferenceManager
                    .getDefaultSharedPreferences(getBaseContext());
            SharedPreferences.Editor e = getPrefs.edit();
            e.putBoolean(getString(R.string.pref_first_time_run), false);
            e.apply();

            loadMainActivity();
        }
    }

    @Override
    public void onSlideChanged() {

    }
}
