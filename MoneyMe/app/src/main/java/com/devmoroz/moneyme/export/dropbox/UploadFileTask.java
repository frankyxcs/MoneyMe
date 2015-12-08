package com.devmoroz.moneyme.export.dropbox;


import android.content.Context;
import android.os.AsyncTask;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxFiles;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class UploadFileTask extends AsyncTask<String, Void, DbxFiles.FileMetadata> {

    private String path;
    private Context context;
    private File file;
    private DbxFiles mFilesClient;
    private Exception mException;
    private Callback mCallback;

    public interface Callback {
        void onUploadComplete(DbxFiles.FileMetadata result);
        void onError(Exception e);
    }

    public UploadFileTask(Context context, DbxFiles mFilesClient, String path, File file, Callback callback) {
        this.path = path;
        this.context = context;
        this.file = file;
        this.mFilesClient = mFilesClient;
        this.mCallback = callback;
    }

    @Override
    protected void onPostExecute(DbxFiles.FileMetadata result) {
        super.onPostExecute(result);
        if (mException != null) {
            mCallback.onError(mException);
        } else if (result == null) {
            mCallback.onError(null);
        } else {
            mCallback.onUploadComplete(result);
        }
    }

    @Override
    protected DbxFiles.FileMetadata doInBackground(String... params) {

        if (file != null) {
            String remoteFileName = file.getName();

            try {
                InputStream inputStream = new FileInputStream(file);
                try {
                    mFilesClient.uploadBuilder(path + "/" + remoteFileName)
                            .mode(DbxFiles.WriteMode.overwrite)
                            .run(inputStream);
                } finally {
                    inputStream.close();
                }
            } catch (DbxException | IOException e) {
                mException = e;
                e.printStackTrace();
            }
        }
        return null;
    }
}
