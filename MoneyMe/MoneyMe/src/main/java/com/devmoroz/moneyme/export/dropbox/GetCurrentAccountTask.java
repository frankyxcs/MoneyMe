package com.devmoroz.moneyme.export.dropbox;

import android.os.AsyncTask;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxUsers;


public class GetCurrentAccountTask extends AsyncTask<Void, Void, DbxUsers.FullAccount> {

    private final DbxUsers mUsersClient;
    private final Callback mCallback;
    private Exception mException;

    public interface Callback {
        void onComplete(DbxUsers.FullAccount result);
        void onError(Exception e);
    }

    public GetCurrentAccountTask(DbxUsers usersClient, Callback callback) {
        mUsersClient = usersClient;
        mCallback = callback;
    }

    @Override
    protected void onPostExecute(DbxUsers.FullAccount account) {
        super.onPostExecute(account);
        if (mException != null) {
            mCallback.onError(mException);
        } else {
            mCallback.onComplete(account);
        }
    }

    @Override
    protected DbxUsers.FullAccount doInBackground(Void... params) {

        try {
            return mUsersClient.getCurrentAccount();

        } catch (DbxException e) {
            mException = e;
            e.printStackTrace();
        }

        return null;
    }
}
