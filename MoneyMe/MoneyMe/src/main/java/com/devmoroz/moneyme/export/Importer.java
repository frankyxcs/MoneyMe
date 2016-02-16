package com.devmoroz.moneyme.export;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.devmoroz.moneyme.MoneyApplication;
import com.devmoroz.moneyme.R;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Importer {

    protected final Context mContext;

    public Importer() {
        mContext = MoneyApplication.getAppContext();
    }

    public void restoreBackup() {

        File[] backupFiles = new File(ExportParams.BACKUP_FOLDER_PATH).listFiles();
        if (backupFiles == null){
            Toast.makeText(mContext, R.string.toast_backup_folder_not_found, Toast.LENGTH_LONG).show();
            new File(ExportParams.BACKUP_FOLDER_PATH).mkdirs();
            return;
        }

        Arrays.sort(backupFiles);
        List<File> backupFilesList = Arrays.asList(backupFiles);
        Collections.reverse(backupFilesList);
        final File[] sortedBackupFiles = (File[]) backupFilesList.toArray();

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(mContext, android.R.layout.select_dialog_singlechoice);
        final DateFormat dateFormatter = SimpleDateFormat.getDateTimeInstance();
        for (File backupFile : sortedBackupFiles) {
            long time = 123;
            if (time > 0)
                arrayAdapter.add(dateFormatter.format(new Date(time)));
            else
                arrayAdapter.add(backupFile.getName());
        }

        AlertDialog.Builder restoreDialogBuilder =  new AlertDialog.Builder(mContext);
        restoreDialogBuilder.setTitle(R.string.title_select_backup_to_restore);
        restoreDialogBuilder.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        restoreDialogBuilder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                File backupFile = sortedBackupFiles[which];
                new ImportAsyncTask(mContext).execute(Uri.fromFile(backupFile));
            }
        });

        restoreDialogBuilder.create().show();
    }
}
