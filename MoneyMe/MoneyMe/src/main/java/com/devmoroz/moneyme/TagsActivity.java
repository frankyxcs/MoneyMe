package com.devmoroz.moneyme;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


public class TagsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppDefault);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tags);
    }
}
