package com.devmoroz.moneyme.export.dropbox;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.devmoroz.moneyme.utils.PicassoClient;
import com.devmoroz.moneyme.utils.Preferences;
import com.dropbox.core.android.Auth;


public abstract class DropboxActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String accessToken = Preferences.getDropboxToken(getApplicationContext());
        if (accessToken == null) {
            accessToken = Auth.getOAuth2Token();
            if (accessToken != null) {
                Preferences.storeDropboxToken(getApplicationContext(),accessToken);
                initAndLoadData(accessToken);
            }
        } else {
            initAndLoadData(accessToken);
        }
    }

    private void initAndLoadData(String accessToken) {
        DropboxClient.init(accessToken);
        PicassoClient.init(getApplicationContext(), DropboxClient.files());
        loadData();
    }

    protected abstract void loadData();

    protected boolean hasToken() {
        String accessToken = Preferences.getDropboxToken(getApplicationContext());
        return accessToken != null;
    }
}
