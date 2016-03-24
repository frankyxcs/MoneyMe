package com.devmoroz.moneyme;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.view.WindowManager;

import com.afollestad.materialdialogs.folderselector.FileChooserDialog;
import com.devmoroz.moneyme.fragments.Intro.FinishFragment1;
import com.devmoroz.moneyme.fragments.Intro.FinishFragment2;
import com.devmoroz.moneyme.fragments.Intro.StartFragment;
import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

import java.io.File;

public class FirstRunIntro extends AppIntro implements FileChooserDialog.FileCallback {

    public static final int REQUEST_PERMISSION_FINISH2 = 22;
    public static final int REQUEST_PERMISSION_FINISH1 = 11;

    private FinishFragment1 finishFragment1;
    private FinishFragment2 finishFragment2;
    private StartFragment startFragment;

    @Override
    public void init(Bundle savedInstanceState) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        doneButton.setEnabled(false);
        setDoneText(getString(R.string.done));
        setSkipText(getString(R.string.back));

        startFragment = StartFragment.newInstance(new StartFragment.Callback() {
            @Override
            public void onRestoreClicked() {
                setup2();
            }

            @Override
            public void onNewUserClicked() {
                setup3();
            }
        });
        finishFragment1 = FinishFragment1.newInstance(doneButton);
        finishFragment2 = FinishFragment2.newInstance(new FinishFragment2.Callback() {
            @Override
            public void onDriveClicked() {
                showDriveDialog();
            }

            @Override
            public void onLocalClicked() {
                showFileChooserDialog();
            }
        });
        addSlide(startFragment);
        addSlide(finishFragment2);
        addSlide(finishFragment1);

        setSeparatorColor(Color.parseColor("#2196F3"));
        setIndicatorColor(Color.parseColor("#2196F3"), Color.parseColor("#2196F3"));
        showStatusBar(false);
        showSkipButton(false);
        setProgressButtonEnabled(false);
        setSwipeLock(true);


    }

    @Override
    public void onSkipPressed() {
        setup1();
    }

    private void showFileChooserDialog() {
        new FileChooserDialog.Builder(this)
                .chooseButton(R.string.choose)
                .mimeType("*/*")
                .cancelButton(R.string.cancel)
                .show();
    }

    private void showDriveDialog() {

    }

    @Override
    public void onBackPressed() {
        if (getPager().getCurrentItem() == 0) {
            finish();
        } else {
            setup1();
        }
    }

    private void loadMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void setup1() {
        getPager().setCurrentItem(0);
        setProgressButtonEnabled(false);
        showSkipButton(false);
        setSeparatorColor(Color.parseColor("#2196F3"));
        setIndicatorColor(Color.parseColor("#2196F3"), Color.parseColor("#2196F3"));
    }

    public void setup2() {
        getPager().setCurrentItem(1);
        setProgressButtonEnabled(false);
        showSkipButton(true);
        setSeparatorColor(Color.WHITE);
        setIndicatorColor(Color.parseColor("#673AB7"), Color.parseColor("#673AB7"));
        askPermissions(REQUEST_PERMISSION_FINISH2);
    }

    public void setup3() {
        getPager().setCurrentItem(2);
        setProgressButtonEnabled(true);
        setSeparatorColor(Color.WHITE);
        setIndicatorColor(Color.parseColor("#3F51B5"), Color.parseColor("#3F51B5"));
        showSkipButton(true);
    }

    @Override
    public void onNextPressed() {

    }

    @Override
    public void onDonePressed() {
        if (finishFragment1.setUpAccount()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                askPermissions(REQUEST_PERMISSION_FINISH1);
            } else {
                removeFirstRunFlag();
                loadMainActivity();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_FINISH1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    removeFirstRunFlag();
                    loadMainActivity();
                } else {
                    removeFirstRunFlag();
                    loadMainActivity();
                }
            }
            break;
            case REQUEST_PERMISSION_FINISH2: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    onBackPressed();
                }
            }
            break;


        }

    }

    @Override
    public void onSlideChanged() {

    }

    private void askPermissions(int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, requestCode);
            } else {
                removeFirstRunFlag();
                loadMainActivity();
            }
        }
    }

    public static void removeFirstRunFlag() {
        Context context = MoneyApplication.getAppContext();
        SharedPreferences getPrefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor e = getPrefs.edit();
        e.putBoolean(context.getString(R.string.pref_first_time_run), false);
        e.commit();
    }

    @Override
    public void onFileSelection(FileChooserDialog dialog, File file) {

    }
}
