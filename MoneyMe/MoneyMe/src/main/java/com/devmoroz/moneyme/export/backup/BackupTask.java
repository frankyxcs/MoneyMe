package com.devmoroz.moneyme.export.backup;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import com.devmoroz.moneyme.export.ExportParams;
import com.devmoroz.moneyme.notification.MoneyMeScheduler;
import com.devmoroz.moneyme.utils.StorageUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class BackupTask extends AsyncTask<String, Void, Integer> {

    public interface CompletionListener {
        void onBackupComplete();
        void onRestoreComplete();
        void onError(int errorCode);
    }

    public static final int BACKUP_SUCCESS = 1;
    public static final int RESTORE_SUCCESS = 2;
    public static final int BACKUP_ERROR = 3;
    public static final int RESTORE_NOFILEERROR = 4;
    public static final String COMMAND_BACKUP = "backupDatabase";
    public static final String COMMAND_RESTORE = "restoreDatabase";
    private Context mContext;
    private CompletionListener listener;

    public BackupTask(Context context, CompletionListener callback) {
        super();
        mContext = context;
        listener = callback;
    }

    public void setCompletionListener(CompletionListener aListener) {
        listener = aListener;
    }

    @Override
    protected Integer doInBackground(String... params) {
        String command = params[0];
        String backupName = params[1];

        //Get a reference to the database
        File dbFile = mContext.getDatabasePath("moneyme.db");

        File backupDir = StorageUtils.getBackupDir();

        File backup = new File(backupDir, backupName);

        if (command.equals(COMMAND_BACKUP)) {
            try {
                fileCopy(dbFile, backup);
                return BACKUP_SUCCESS;
            } catch (IOException e) {
                return BACKUP_ERROR;
            }
        } else if (command.equals(COMMAND_RESTORE)) {
            try {
                if (!backup.exists()) {
                    return RESTORE_NOFILEERROR;
                }
                fileCopy(backup, dbFile);
                resetTodoReminders();
                return RESTORE_SUCCESS;
            } catch (IOException e) {
                return BACKUP_ERROR;
            }
        } else {
            return BACKUP_ERROR;
        }
    }

    @Override
    protected void onPostExecute(Integer result) {
        switch (result) {
            case BACKUP_SUCCESS:
                if (listener != null) {
                    listener.onBackupComplete();
                }
                break;
            case RESTORE_SUCCESS:
                if (listener != null) {
                    listener.onRestoreComplete();
                }
                break;
            case RESTORE_NOFILEERROR:
                if (listener != null) {
                    listener.onError(RESTORE_NOFILEERROR);
                }
                break;
            default:
                if (listener != null) {
                    listener.onError(BACKUP_ERROR);
                }
        }
    }

    private void fileCopy(File source, File dest) throws IOException {
        FileChannel inChannel = new FileInputStream(source).getChannel();
        FileChannel outChannel = new FileOutputStream(dest).getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } finally {
            if (inChannel != null)
                inChannel.close();
            if (outChannel != null)
                outChannel.close();
        }
    }

    private void resetTodoReminders(){
        long now = System.currentTimeMillis() + 1000;
        MoneyMeScheduler scheduler = new MoneyMeScheduler();
        scheduler.scheduleAllTodo(mContext,now);
    }
}
