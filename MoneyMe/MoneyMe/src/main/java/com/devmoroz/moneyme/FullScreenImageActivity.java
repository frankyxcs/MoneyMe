package com.devmoroz.moneyme;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.WindowManager;

import com.bumptech.glide.Glide;
import com.devmoroz.moneyme.utils.Constants;
import com.devmoroz.moneyme.utils.FormatUtils;
import com.devmoroz.moneyme.widgets.TouchImageView;

public class FullScreenImageActivity extends AppCompatActivity{

    private TouchImageView entityImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.FullScreenImageStyle);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_image);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        entityImageView = (TouchImageView) findViewById(R.id.entityImageView);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.fullscreen_image_toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String path = intent.getStringExtra(Constants.IMAGE_PATH);

        if(FormatUtils.isNotEmpty(path)){
            Glide.with(this).load(path).into(entityImageView);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
