package com.devmoroz.moneyme.export;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.content.Context;

import java.io.InputStream;


public class ImportAsyncTask extends AsyncTask<Uri, Void, Boolean> {
    private final Context mContext;
    private Callback mCallback;

    public ImportAsyncTask(Context context){
        this.mContext = context;
    }

    public interface Callback {
        public void onTaskComplete();
    }

    public ImportAsyncTask(Activity context, Callback callback){
        this.mContext = context;
        this.mCallback = callback;
    }


    @Override
    protected Boolean doInBackground(Uri... uris) {
        try {
            InputStream accountInputStream = mContext.getContentResolver().openInputStream(uris[0]);

        } catch (Exception exception){


            return false;
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean importSuccess) {
        if (mCallback != null)
            mCallback.onTaskComplete();

    }
}
