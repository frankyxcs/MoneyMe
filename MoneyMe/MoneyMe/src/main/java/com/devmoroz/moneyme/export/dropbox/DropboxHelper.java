package com.devmoroz.moneyme.export.dropbox;


import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.devmoroz.moneyme.utils.Constants;
import com.devmoroz.moneyme.utils.Preferences;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DropboxHelper {

    private static DropboxHelper mHelper;
    private static Context mContext;

    public Date getDateLastModified() throws ParseException {
        String stringDate = Preferences.getDropboxLastSyncDate(mContext);
        if (TextUtils.isEmpty(stringDate)) return null;
        return new SimpleDateFormat(Constants.SYNC_DATE_FORMAT).parse(stringDate);
    }

    public void setDateLastModified(Date date) {
       Preferences.storeDropboxLastSyncDate(mContext,date);
    }

    public String getLinkedRemoteFile() {
        return Preferences.getDropboxFilePath(mContext);
    }

    public void setLinkedRemoteFile(String fileDropbox) {
        Preferences.storeDropboxFilePath(mContext,fileDropbox);
    }
}
