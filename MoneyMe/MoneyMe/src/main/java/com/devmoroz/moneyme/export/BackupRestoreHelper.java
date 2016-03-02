package com.devmoroz.moneyme.export;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.design.widget.CoordinatorLayout;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.devmoroz.moneyme.MoneyApplication;
import com.devmoroz.moneyme.R;
import com.devmoroz.moneyme.export.backup.BackupTask;
import com.devmoroz.moneyme.helpers.PermissionsHelper;
import com.devmoroz.moneyme.logging.L;
import com.devmoroz.moneyme.utils.AppUtils;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class BackupRestoreHelper {

    protected final Context mContext;

    public BackupRestoreHelper(Context context) {
        this.mContext = context;
    }

    public void Restore(Activity activity, View view){
        PermissionsHelper.requestPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE, R
                .string.permission_external_storage, view, this::doRestore);
    }

    private void doRestore() {
        File[] backupFiles = new File(ExportParams.BACKUP_FOLDER_PATH).listFiles();
        if (backupFiles == null) {
            L.T(mContext, mContext.getString(R.string.toast_backup_folder_not_found));
            return;
        }

        Arrays.sort(backupFiles);
        List<File> backupFilesList = Arrays.asList(backupFiles);
        Collections.reverse(backupFilesList);
        final String[] listNames = new String[backupFilesList.size()];
        int i = 0;
        for (File obj : backupFilesList) {
            listNames[i] = obj.getName();
            i++;
        }
        final int progressContent = R.string.restore_database_inprogress;
        MaterialDialog.Builder restoreDialogBuilder = new MaterialDialog.Builder(mContext);
        restoreDialogBuilder.title(R.string.title_select_backup_to_restore)
                .items(listNames)
                .positiveText(R.string.ok)
                .dividerColorRes(R.color.colorPrimaryDark)
                .positiveColorRes(R.color.colorPrimary)
                .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                        String backupName = listNames[i];

                        final ProgressDialog dialog = new ProgressDialog(mContext);
                        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        dialog.setCancelable(false);
                        dialog.setMessage(mContext.getString(progressContent));
                        dialog.show();

                        new BackupTask(mContext, new BackupTask.CompletionListener() {
                            @Override
                            public void onBackupComplete() {
                                dialog.dismiss();
                                L.T(mContext, mContext.getString(R.string.backup_completed));
                            }

                            @Override
                            public void onRestoreComplete() {
                                dialog.dismiss();
                                L.T(mContext, mContext.getString(R.string.restore_completed));
                                AppUtils.restartApp(MoneyApplication.getAppContext());
                            }

                            @Override
                            public void onError(int errorCode) {
                                dialog.dismiss();
                                if (errorCode == BackupTask.RESTORE_NOFILEERROR) {
                                    L.T(mContext, mContext.getString(R.string.restore_failed));
                                }
                                L.T(mContext, "Something went wrong.Please,try again.");
                            }
                        }).execute(BackupTask.COMMAND_RESTORE, backupName);
                        return true;
                    }
                });

        restoreDialogBuilder.build().show();
    }

    public void Backup(Activity activity, View view){
        PermissionsHelper.requestPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE, R
                .string.permission_external_storage, view, this::doBackup);
    }

    private void doBackup() {
        int content = R.string.backup_dialog_content;
        final int progressContent = R.string.backup_database_inprogress;
        new MaterialDialog.Builder(mContext)
                .title(R.string.backup)
                .content(content)
                .positiveText(R.string.ok_continue)
                .negativeText(R.string.cancel)
                .positiveColorRes(R.color.colorPrimary)
                .negativeColorRes(R.color.colorPrimary)
                .widgetColorRes(R.color.colorPrimary)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {

                        final ProgressDialog dialog = new ProgressDialog(mContext);
                        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        dialog.setCancelable(false);
                        dialog.setMessage(mContext.getString(progressContent));
                        dialog.show();

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd-HH.mm.ss");
                        String fileName = sdf.format(Calendar.getInstance().getTime());

                        new BackupTask(mContext, new BackupTask.CompletionListener() {
                            @Override
                            public void onBackupComplete() {
                                dialog.dismiss();
                                L.T(mContext, mContext.getString(R.string.backup_completed));
                            }

                            @Override
                            public void onRestoreComplete() {
                                dialog.dismiss();
                                L.T(mContext, mContext.getString(R.string.restore_completed));
                                AppUtils.restartApp(MoneyApplication.getAppContext());
                            }

                            @Override
                            public void onError(int errorCode) {
                                dialog.dismiss();
                                if (errorCode == BackupTask.RESTORE_NOFILEERROR) {
                                    L.T(mContext, mContext.getString(R.string.restore_failed));
                                }
                                L.T(mContext, "Something went wrong.Please,try again.");
                            }
                        }).execute(BackupTask.COMMAND_BACKUP, fileName);
                    }
                })
                .show();
    }

    public void shadowBackup(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd-HH.mm.ss");
        String fileName = sdf.format(Calendar.getInstance().getTime());

        new BackupTask(mContext, new BackupTask.CompletionListener() {
            @Override
            public void onBackupComplete() {
                L.T(mContext, mContext.getString(R.string.backup_completed));
            }

            @Override
            public void onRestoreComplete() {
                L.T(mContext, mContext.getString(R.string.restore_completed));
            }

            @Override
            public void onError(int errorCode) {
                if (errorCode == BackupTask.RESTORE_NOFILEERROR) {
                    L.T(mContext, mContext.getString(R.string.restore_failed));
                }
                L.T(mContext, "Something went wrong.Please,try again.");
            }
        }).execute(BackupTask.COMMAND_BACKUP, fileName);
    }
}
