package com.devmoroz.moneyme;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.EditText;

public class AddItemActivity extends AppCompatActivity {

    private EditText amount;
    private EditText description;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        toolbar = (Toolbar) findViewById(R.id.add_activity_toolbar);
        if(toolbar != null){
            toolbar.setTitle(getIntent().getIntExtra("toolbar_header_text",R.string.default_add_toolbar_name));
            setSupportActionBar(toolbar);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        amount = (EditText) findViewById(R.id.add_activity_amount);
        description = (EditText) findViewById(R.id.add_activity_description);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }
}
