package com.devmoroz.moneyme.export;

import android.content.Context;
import android.os.Environment;

import com.devmoroz.moneyme.export.backup.DBExport;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class ExportParams {

    public enum ExportTarget {SD_CARD, SHARING, DROPBOX, DRIVE}


    public enum ExportType {
        Backup {
            @Override public Exporter getDataExporter(Context context,OutputStream outputStream) {
                return new DBExport(context, outputStream);
            }

            @Override public String getMimeType() {
                return "application/json";
            }

            @Override
            public String getExtention() {
                return ".json";
            }
        },
        CSV {
            @Override public Exporter getDataExporter(Context context,OutputStream outputStream) {
                return new CSVexport(context, outputStream);
            }

            @Override public String getMimeType() {
                return "text/csv";
            }

            @Override
            public String getExtention() {
                return ".csv";
            }
        };

        public abstract Exporter getDataExporter(Context context,OutputStream outputStream);

        public abstract String getMimeType();

        public abstract String getExtention();
    }



    private static final String BASE_FOLDER_PATH = Environment.getExternalStorageDirectory() + "/" + "MoneyMe";

    public static final String EXPORT_FOLDER_PATH =  BASE_FOLDER_PATH + "/exports/";

    public static final String BACKUP_FOLDER_PATH = BASE_FOLDER_PATH + "/backups/";

    public static final String RECEIPTS_FOLDER_PATH = BASE_FOLDER_PATH + "/receipts/";

    private ExportTarget mExportTarget = ExportTarget.SHARING;
    private ExportType mExportType = ExportType.Backup;

    public ExportParams(ExportTarget mExportTarget, ExportType mExportType) {
        this.mExportTarget = mExportTarget;
        this.mExportType = mExportType;
    }

    public ExportTarget getExportTarget() {
        return mExportTarget;
    }

    public ExportType getExportType() {
        return mExportType;
    }

    public void setExportTarget(ExportTarget mExportTarget) {
        this.mExportTarget = mExportTarget;
    }

}
