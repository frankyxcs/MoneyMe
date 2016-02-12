package com.devmoroz.moneyme.export.dropbox;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.widget.Toast;

import com.afollestad.materialdialogs.AlertDialogWrapper;


public class CheckDropboxForUpdatesTask extends AsyncTask<Void, Integer, Integer> {

    public CheckDropboxForUpdatesTask(Context context, DropboxHelper helper) {
        mContext = context;
        mDropboxHelper = helper;
    }

    private Context mContext;
    private DropboxHelper mDropboxHelper;

    @Override
    protected Integer doInBackground(Void... voids) {
        try {
            publishProgress(1);

            return 1;
        } catch (Exception e) {
            throw new RuntimeException("Error in checkDropboxForUpdates", e);
        }
    }

    @Override
    protected void onProgressUpdate(Integer... params) {
        //Toast.makeText(mContext, R.string.checking_dropbox_for_changes, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPostExecute(Integer ret) {

    }

    private void showNotificationDialog() {
        /*new AlertDialogWrapper.Builder(mContext)
                // setting alert dialog
                .setIcon(FontIconDrawable.inflate(mContext, R.xml.ic_alert))
                .setTitle(R.string.update_available)
                .setMessage(R.string.update_on_dropbox)
                .setNeutralButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DropboxManager dropbox = new DropboxManager(mContext, mDropboxHelper);
                        dropbox.synchronizeDropbox();
                        dialog.dismiss();
                    }
                })
                .show();*/
    }
}
