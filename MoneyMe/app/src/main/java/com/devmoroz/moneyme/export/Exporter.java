package com.devmoroz.moneyme.export;


import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.devmoroz.moneyme.MoneyApplication;

import java.io.OutputStream;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public abstract class Exporter {

    private static final String BASE_FOLDER_PATH = Environment.getExternalStorageDirectory() + "/" + "MoneyMe";

    public static final String EXPORT_FOLDER_PATH =  BASE_FOLDER_PATH + "/exports/";

    public static final String BACKUP_FOLDER_PATH = BASE_FOLDER_PATH + "/backups/";

    private static final SimpleDateFormat EXPORT_FILENAME_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US);

    public static final String TIMESTAMP_ZERO = new Timestamp(0).toString();

    protected final Context mContext;


    protected Exporter(Context context) {
        this.mContext = context;
    }

    public static String buildExportFilename() {
        return EXPORT_FILENAME_DATE_FORMAT.format(new Date(System.currentTimeMillis()))
                + "_moneyme_export" + ".csv";
    }

    public static long getExportTime(String filename){
        String[] tokens = filename.split("_");
        long timeMillis = 0;
        if (tokens.length < 2){
            return timeMillis;
        }
        try {
            Date date = EXPORT_FILENAME_DATE_FORMAT.parse(tokens[0] + "_" + tokens[1]);
            timeMillis = date.getTime();
        } catch (ParseException e) {

        }
        return timeMillis;
    }

    public abstract void generateExport(OutputStream outputStream) throws Exception;

    protected abstract String getExtension();
}
