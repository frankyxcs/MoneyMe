package com.devmoroz.moneyme;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.devmoroz.moneyme.export.dropbox.DropboxActivity;
import com.devmoroz.moneyme.export.dropbox.DropboxClientFactory;
import com.devmoroz.moneyme.export.dropbox.DropboxFilesAdapter;
import com.devmoroz.moneyme.export.dropbox.ListFolderTask;
import com.devmoroz.moneyme.utils.PicassoClient;
import com.dropbox.core.v2.DbxFiles;

public class DropboxSyncActivity extends DropboxActivity {

    public final static String EXTRA_PATH = "FilesActivity_Path";
    private String mPath;

    private DropboxFilesAdapter mFilesAdapter;

    @Override
    protected void loadData() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);
        dialog.setMessage("Loading");
        dialog.show();

        new ListFolderTask(DropboxClientFactory.files(), new ListFolderTask.Callback() {
            @Override
            public void onDataLoaded(DbxFiles.ListFolderResult result) {
                dialog.dismiss();
                mFilesAdapter.setFiles(result.entries);
            }

            @Override
            public void onError(Exception e) {
                dialog.dismiss();
            }
        }).execute(mPath);
    }

    public static Intent getIntent(Context context, String path) {
        Intent filesIntent = new Intent(context, DropboxSyncActivity.class);
        filesIntent.putExtra(DropboxSyncActivity.EXTRA_PATH, path);
        return filesIntent;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case (android.R.id.home):
                super.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppDefault);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dropbox_files);

        String path = getIntent().getStringExtra(EXTRA_PATH);
        mPath = path == null ? "" : path;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarDropbox);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.files_list);
        mFilesAdapter = new DropboxFilesAdapter(getApplicationContext(), PicassoClient.getPicasso(), new DropboxFilesAdapter.Callback(){
            @Override
            public void onFolderClicked(DbxFiles.FolderMetadata folder) {
                startActivity(DropboxSyncActivity.getIntent(DropboxSyncActivity.this, folder.pathLower));
            }

            @Override
            public void onFileClicked(DbxFiles.FileMetadata file) {

            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mFilesAdapter);
    }
}
