package com.devmoroz.moneyme;


import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.view.WindowManager;

import com.devmoroz.moneyme.fragments.Intro.FinishFragment;
import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntro2;

public class FirstRunIntro extends AppIntro2 {

    public static final int REQUEST_PERMISSION_WRITE_SD_CARD = 0xAB;

    private FinishFragment finishFragment;

    @Override
    public void init(Bundle savedInstanceState) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE }, REQUEST_PERMISSION_WRITE_SD_CARD);
            }else{
                loadMainActivity();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode){
            case REQUEST_PERMISSION_WRITE_SD_CARD:{
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadMainActivity();
                } else {
                    loadMainActivity();
                }
            }
        }

    }

    @Override
    public void onSlideChanged() {

    }
}
