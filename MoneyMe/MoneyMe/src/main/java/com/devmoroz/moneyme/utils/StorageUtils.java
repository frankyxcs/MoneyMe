package com.devmoroz.moneyme.utils;


import android.os.Environment;

import java.io.File;

public class StorageUtils {

    private static final String BASE_FOLDER_PATH = Environment.getExternalStorageDirectory() + "/" + "MoneyMe";

    public static final String EXPORT_FOLDER_PATH =  BASE_FOLDER_PATH + "/exports/";

    public static final String BACKUP_FOLDER_PATH = BASE_FOLDER_PATH + "/backups/";

    public static boolean checkStorage() {
        boolean mExternalStorageAvailable;
        boolean mExternalStorageWriteable;
        String state = Environment.getExternalStorageState();

        switch (state) {
            case Environment.MEDIA_MOUNTED:
                // We can read and write the media
                mExternalStorageAvailable = mExternalStorageWriteable = true;
                break;
            case Environment.MEDIA_MOUNTED_READ_ONLY:
                // We can only read the media
                mExternalStorageAvailable = true;
                mExternalStorageWriteable = false;
                break;
            default:
                // Something else is wrong. It may be one of many other states, but
                // all we need
                // to know is we can neither read nor write
                mExternalStorageAvailable = mExternalStorageWriteable = false;
                break;
        }
        return mExternalStorageAvailable && mExternalStorageWriteable;
    }

    public static File getBackupDir() {
        File backupDir = new File(BACKUP_FOLDER_PATH);
        if (!backupDir.exists())
            backupDir.mkdirs();
        return backupDir;
    }
}
