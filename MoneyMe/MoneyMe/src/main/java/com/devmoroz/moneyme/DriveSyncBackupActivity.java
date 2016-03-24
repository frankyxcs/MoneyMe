package com.devmoroz.moneyme;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.devmoroz.moneyme.export.ExportAsyncTask;
import com.devmoroz.moneyme.export.ExportParams;
import com.devmoroz.moneyme.logging.L;
import com.devmoroz.moneyme.utils.Preferences;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.AccountPicker;

public class DriveSyncBackupActivity extends AppCompatActivity {

    private static final String[] ACCOUNT_TYPE = new String[] {GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE};
    private static final int CHOOSE_ACCOUNT = 117;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void chooseAccount() {
        try {
            Intent intent = AccountPicker.newChooseAccountIntent(null, null, ACCOUNT_TYPE, true,
                    null, null, null, null);
            startActivityForResult(intent, CHOOSE_ACCOUNT);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Ooooops", Toast.LENGTH_LONG).show();

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CHOOSE_ACCOUNT:
                    if (data != null) {
                        Bundle b = data.getExtras();
                        String accountName = b.getString(AccountManager.KEY_ACCOUNT_NAME);
                        Log.d("Preferences", "Selected account: " + accountName);
                        if (accountName != null && accountName.length() > 0) {
                            Preferences.storeGoogleDriveAccount(this, accountName);
                        }
                    }
                    break;
            }
        }
    }

    private void showDriveBackupDialog() {
        new MaterialDialog.Builder(this)
                .title(R.string.csv_export)
                .content(R.string.csv_export_dialog_content)
                .positiveText(R.string.ok_continue)
                .negativeText(R.string.cancel)
                .positiveColorRes(R.color.colorPrimary)
                .negativeColorRes(R.color.colorPrimary)
                .widgetColorRes(R.color.colorPrimary)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                        final ProgressDialog dialog = new ProgressDialog(DriveSyncBackupActivity.this);
                        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        dialog.setCancelable(false);
                        dialog.setMessage(getString(R.string.csv_export_inprogress));
                        dialog.show();
                        ExportParams exportParams = new ExportParams(ExportParams.ExportTarget.DRIVE, ExportParams.ExportType.Backup);
                        new ExportAsyncTask(DriveSyncBackupActivity.this, dialog, new ExportAsyncTask.CompletionListener() {
                            @Override
                            public void onExportComplete() {
                                dialog.dismiss();
                                L.T(DriveSyncBackupActivity.this, getString(R.string.csv_export_completed));
                            }

                            @Override
                            public void onError(int errorCode) {
                                L.T(DriveSyncBackupActivity.this, "Бляха, что-то не получилось");
                                dialog.dismiss();
                            }
                        }).execute(exportParams);
                    }
                })
                .show();
    }
}