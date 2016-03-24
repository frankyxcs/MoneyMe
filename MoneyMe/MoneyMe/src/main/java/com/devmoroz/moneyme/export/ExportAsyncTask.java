package com.devmoroz.moneyme.export;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.devmoroz.moneyme.R;
import com.devmoroz.moneyme.export.drive.GoogleDriveClient;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExportAsyncTask extends AsyncTask<ExportParams, Void, Integer> {

    public static final int SUCCESS = 1;
    public static final int ERROR = 2;

    private final Context mContext;

    private ExportParams mExportParams;

    private Exporter dataExporter;

    private ProgressDialog dialog;

    private CompletionListener listener;

    private GoogleDriveClient googleDriveClient;

    public interface CompletionListener {
        void onExportComplete();

        void onError(int errorCode);
    }

    public ExportAsyncTask(Context context, ProgressDialog dialog, CompletionListener callback) {
        this.mContext = context;
        this.listener = callback;
        this.dialog = dialog;
    }

    public ExportAsyncTask setGoogleDriveClient(GoogleDriveClient googleDriveClient) {
        this.googleDriveClient = googleDriveClient;
        return this;
    }

    @Override
    protected Integer doInBackground(ExportParams... params) {
        try {
            mExportParams = params[0];
            File exportDir =
                    new File(ExportParams.EXPORT_FOLDER_PATH);
            if (!exportDir.exists()) {
                exportDir.mkdirs();
            }

            final File file = getFile(exportDir, mExportParams.getExportType().getExtention());
            final OutputStream outputStream = new FileOutputStream(file);
            dataExporter = mExportParams.getExportType().getDataExporter(mContext, outputStream);
            dataExporter.exportData();
            dialog.dismiss();
            switch (mExportParams.getExportTarget()) {
                case SHARING:
                    shareFile(file, mExportParams.getExportType().getMimeType());
                    return SUCCESS;
                case DROPBOX:
                    sendExportToDropbox(file);
                    return SUCCESS;
                case DRIVE:
                    sendExportToDrive(file);
                    return SUCCESS;
                case SD_CARD:
                    moveFile(file.getAbsolutePath(), ExportParams.BACKUP_FOLDER_PATH);
                    return SUCCESS;
            }
        } catch (final Exception e) {
            return ERROR;
        }
        return ERROR;
    }

    private void sendExportToDrive(File file) {
        if (googleDriveClient != null) {
            try {
                int size = (int) file.length();
                byte[] bytes = new byte[size];
                BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
                buf.read(bytes, 0, bytes.length);
                buf.close();
                googleDriveClient.doDriveBackup(file.getName(), bytes);
            }
            catch (IOException ex){

            }
        }
    }

    @Override
    protected void onPostExecute(Integer result) {
        switch (result) {
            case SUCCESS:
                if (listener != null) {
                    listener.onExportComplete();
                }
                break;
            case ERROR:
                if (listener != null) {
                    listener.onError(1);
                }
                break;
            default:
                if (listener != null) {
                    listener.onError(2);
                }
        }
    }

    private File getFile(File directory, String extension) {
        return new File(directory, getFileTitle(extension));
    }

    private String getFileTitle(String extension) {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        return mContext.getString(R.string.app_name) + "_" + dateFormat.format(new Date()) + extension;
    }

    private void sendExportToDropbox(File file) {


    }

    private String stripPathPart(String fullPathName) {
        return (new File(fullPathName)).getName();
    }

    public void moveFile(String src, String dst) throws IOException {
        File srcFile = new File(src);
        File dstFile = new File(dst);
        FileChannel inChannel = new FileInputStream(srcFile).getChannel();
        FileChannel outChannel = new FileOutputStream(dstFile).getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } finally {
            if (inChannel != null)
                inChannel.close();
            outChannel.close();
        }
        srcFile.delete();
    }

    private void shareFile(File file, String type) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);

        Uri exportFile = Uri.fromFile(file);
        shareIntent.putExtra(Intent.EXTRA_STREAM, exportFile);
        shareIntent.setType(type);

        if (mContext instanceof Activity) {
            List<ResolveInfo> activities = mContext.getPackageManager().queryIntentActivities(shareIntent, 0);
            if (activities != null && !activities.isEmpty()) {
                mContext.startActivity(Intent.createChooser(shareIntent,
                        mContext.getString(R.string.title_select_export_destination)));
            } else {

            }
        }
    }

    @NonNull
    private ArrayList<Uri> convertFilePathsToUris(List<String> paths) {
        ArrayList<Uri> exportFiles = new ArrayList<>();

        for (String path : paths) {
            File file = new File(path);
            file.setReadable(true, false);
            exportFiles.add(Uri.fromFile(file));
//            exportFiles.add(Uri.parse("file://" + file));
        }
        return exportFiles;
    }
}
