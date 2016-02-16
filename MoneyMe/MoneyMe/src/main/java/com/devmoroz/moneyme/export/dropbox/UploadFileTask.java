package com.devmoroz.moneyme.export.dropbox;


import android.content.Context;
import android.os.AsyncTask;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.DbxFiles;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class UploadFileTask extends AsyncTask<String, Void, DbxFiles.FileMetadata> {

    private Context mContext;
    private final DbxClientV2 mDbxClient;
    private final Callback mCallback;
    private Exception mException;

    public interface Callback {
        void onUploadComplete(DbxFiles.FileMetadata result);
        void onError(Exception e);
    }

    public UploadFileTask(Context context, DbxClientV2 dbxClient, Callback callback) {
        mContext = context;
        mDbxClient = dbxClient;
        mCallback = callback;
    }

    @Override
    public void onPostExecute(DbxFiles.FileMetadata result) {
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
    public DbxFiles.FileMetadata doInBackground(String... params) {
        String exportedFilePath = params[0];
        File exportedFile = new File(exportedFilePath);
        if (exportedFile != null) {
            String remoteFolderPath = params[1];

            try {
                InputStream inputStream = new FileInputStream(exportedFile);
                try {
                    return mDbxClient.files.uploadBuilder(remoteFolderPath + "/" + exportedFile.getName())
                            .mode(DbxFiles.WriteMode.add)
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
