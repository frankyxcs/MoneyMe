package com.devmoroz.moneyme;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.devmoroz.moneyme.helpers.DBHelper;
import com.devmoroz.moneyme.models.Income;
import com.devmoroz.moneyme.models.Outcome;

import java.sql.SQLException;
import java.util.Date;

public class AddItemActivity extends AppCompatActivity {

    private EditText amount;
    private EditText description;
    private Button buttonAdd;
    private Toolbar toolbar;
    private DBHelper dbHelper;

    private int actionType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        toolbar = (Toolbar) findViewById(R.id.add_activity_toolbar);
        actionType = getIntent().getIntExtra("toolbar_header_text",1);
        if(toolbar != null){
            toolbar.setTitle(getIntent().getIntExtra("toolbar_header_text",R.string.default_add_toolbar_name));
            setSupportActionBar(toolbar);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        amount = (EditText) findViewById(R.id.add_activity_amount);
        description = (EditText) findViewById(R.id.add_activity_description);
        buttonAdd = (Button) findViewById(R.id.add_activity_save);

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                switch (actionType){
                    case R.string.income_toolbar_name:
                        try{addIncome();}
                        catch (SQLException ex){}
                        intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        return;
                    case R.string.outcome_toolbar_name:
                        try{addOutcome();}
                        catch (SQLException ex){}
                        intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        return;
                }
            }
        });
    }

    private void addIncome() throws java.sql.SQLException {
        Income income = new Income("",description.getText().toString(),new Date(),Double.parseDouble(amount.getText().toString()),1);
        dbHelper = MoneyApplication.getInstance().GetDBHelper();
        dbHelper.getIncomeDAO().create(income);
    }
    private void addOutcome() throws java.sql.SQLException {
        Outcome outcome = new Outcome("",description.getText().toString(),new Date(),Double.parseDouble(amount.getText().toString()),1);
        dbHelper = MoneyApplication.getInstance().GetDBHelper();
        dbHelper.getOutcomeDAO().create(outcome);
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
