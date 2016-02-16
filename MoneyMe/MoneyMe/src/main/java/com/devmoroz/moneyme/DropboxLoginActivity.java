package com.devmoroz.moneyme;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.devmoroz.moneyme.export.dropbox.DropboxActivity;
import com.devmoroz.moneyme.export.dropbox.DropboxClientFactory;
import com.devmoroz.moneyme.export.dropbox.GetCurrentAccountTask;
import com.dropbox.core.android.Auth;
import com.dropbox.core.v2.DbxUsers;

public class DropboxLoginActivity extends DropboxActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppDefault);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dropbox_login);

        Button loginButton = (Button)findViewById(R.id.login_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Auth.startOAuth2Authentication(DropboxLoginActivity.this, getString(R.string.app_key));
            }
        });

        Button filesButton = (Button)findViewById(R.id.files_button);
        filesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(DropboxSyncActivity.getIntent(DropboxLoginActivity.this, ""));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (hasToken()) {
            findViewById(R.id.login_button).setEnabled(false);
            findViewById(R.id.files_button).setEnabled(true);
        } else {
            findViewById(R.id.login_button).setEnabled(true);
            findViewById(R.id.files_button).setEnabled(false);
        }
    }

    @Override
    protected void loadData() {

        new GetCurrentAccountTask(DropboxClientFactory.users(), new GetCurrentAccountTask.Callback() {
            @Override
            public void onComplete(DbxUsers.FullAccount result) {
                ((TextView) findViewById(R.id.email_text)).setText(result.email);
                ((TextView) findViewById(R.id.name_text)).setText(result.name.displayName);
                ((TextView) findViewById(R.id.type_text)).setText(result.accountType.toString());
            }

            @Override
            public void onError(Exception e) {

            }
        }).execute();
    }

}
