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
import com.devmoroz.moneyme.export.dropbox.DropboxClientFactory;
import com.devmoroz.moneyme.export.dropbox.UploadFileTask;
import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxFiles;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExportAsyncTask extends AsyncTask<ExportParams, Void, Boolean> {

    private final Context mContext;

    private ProgressDialog mProgressDialog;

    private ExportParams mExportParams;

    private List<String> mExportedFiles;

    private Exporter mExporter;

    public ExportAsyncTask(Context context){
        this.mContext = context;
    }

    @Override
    protected Boolean doInBackground(ExportParams... params) {
        mExportParams = params[0];
        try {

        } catch (final Exception e) {

        }

        switch (mExportParams.getExportTarget()) {
            case SHARING:
                List<String> sdCardExportedFiles = moveExportToSDCard();
                shareFiles(sdCardExportedFiles);
                return true;

            case DROPBOX:
                sendExportToDropbox();
                return true;

            case SD_CARD:
                moveExportToSDCard();
                return true;
        }

        return false;
    }

    private void sendExportToDropbox() {

        new UploadFileTask(mContext, DropboxClientFactory.getClient(), new UploadFileTask.Callback() {
            @Override
            public void onUploadComplete(DbxFiles.FileMetadata result) {

            }

            @Override
            public void onError(Exception e) {

            }
        }).execute("", "");
    }

    private List<String> moveExportToSDCard() {
        new File(Exporter.EXPORT_FOLDER_PATH).mkdirs();
        List<String> dstFiles = new ArrayList<>();
        for (String src: mExportedFiles) {
            String dst = Exporter.EXPORT_FOLDER_PATH + stripPathPart(src);
            try {
                moveFile(src, dst);
                dstFiles.add(dst);
            } catch (IOException e) {

            }
        }

        return dstFiles;
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

    private void shareFiles(List<String> paths) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        shareIntent.setType("text/csv");

        ArrayList<Uri> exportFiles = convertFilePathsToUris(paths);
        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, exportFiles);

        shareIntent.putExtra(Intent.EXTRA_SUBJECT, mContext.getString(R.string.title_export_email,
               "CSV"));

        String defaultEmail = PreferenceManager.getDefaultSharedPreferences(mContext)
                .getString(mContext.getString(R.string.pref_default_export_email), null);
        if (defaultEmail != null && defaultEmail.trim().length() > 0)
            shareIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{defaultEmail});

        SimpleDateFormat formatter = (SimpleDateFormat) SimpleDateFormat.getDateTimeInstance();
        ArrayList<CharSequence> extraText = new ArrayList<>();
        extraText.add(mContext.getString(R.string.description_export_email)
                + " " + formatter.format(new Date(System.currentTimeMillis())));
        shareIntent.putExtra(Intent.EXTRA_TEXT, extraText);

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
