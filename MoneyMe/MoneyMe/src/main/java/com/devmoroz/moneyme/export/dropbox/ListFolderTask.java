package com.devmoroz.moneyme.export.dropbox;

import android.os.AsyncTask;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxFiles;


public class ListFolderTask extends AsyncTask<String, Void, DbxFiles.ListFolderResult> {

    private final DbxFiles mFilesClient;
    private Exception mException;
    private Callback mCallback;

    public interface Callback {
        void onDataLoaded(DbxFiles.ListFolderResult result);

        void onError(Exception e);
    }

    public ListFolderTask(DbxFiles filesClient, Callback callback) {
        mFilesClient = filesClient;
        mCallback = callback;
    }

    @Override
    protected void onPostExecute(DbxFiles.ListFolderResult result) {
        super.onPostExecute(result);

        if (mException != null) {
            mCallback.onError(mException);
        } else {
            mCallback.onDataLoaded(result);
        }
    }

    @Override
    protected DbxFiles.ListFolderResult doInBackground(String... params) {
        try {
            return mFilesClient.listFolder(params[0]);
        } catch (DbxException e) {
            mException = e;
            e.printStackTrace();
        }

        return null;
    }
}
