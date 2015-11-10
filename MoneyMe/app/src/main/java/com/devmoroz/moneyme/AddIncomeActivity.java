package com.devmoroz.moneyme;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.devmoroz.moneyme.helpers.DBHelper;
import com.devmoroz.moneyme.models.CreatedItem;
import com.devmoroz.moneyme.models.Income;
import com.devmoroz.moneyme.utils.Constants;
import com.devmoroz.moneyme.utils.CurrencyCache;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddIncomeActivity extends AppCompatActivity {

    private DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

    private EditText amount;
    private EditText description;
    private FloatingActionButton buttonAdd;
    private TextInputLayout floatingAmountLabel;
    private TextView date;
    private Spinner categorySpin;
    private Toolbar toolbar;
    private ImageView chequeImage;


    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTheme(R.style.AppDefaultOutcome);
        setContentView(R.layout.activity_add_income);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fab_grow);

        toolbar = (Toolbar) findViewById(R.id.add_income_toolbar);
        if (toolbar != null) {
            toolbar.setTitle(R.string.income_toolbar_name);
            toolbar.setTitleTextColor(Color.WHITE);
            setSupportActionBar(toolbar);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        amount = (EditText) findViewById(R.id.add_income_amount);
        description = (EditText) findViewById(R.id.add_income_note);
        date = (TextView) findViewById(R.id.add_income_date);
        categorySpin = (Spinner) findViewById(R.id.add_income_category);
        date.setText(dateFormat.format(new Date()));
        buttonAdd = (FloatingActionButton) findViewById(R.id.add_income_save);
        floatingAmountLabel = (TextInputLayout) findViewById(R.id.text_input_layout_income_amount);

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(amount.getText().toString().isEmpty()){
                    floatingAmountLabel.setError(getString(R.string.outcome_amount_required));
                    floatingAmountLabel.setErrorEnabled(true);
                    return;
                }
                Intent intent;
                CreatedItem info = new CreatedItem(-1,"",0);
                try {
                    info = addIncome();
                } catch (SQLException ex) {
                }
                intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra(Constants.CREATED_ITEM_ID, info.getItemId());
                intent.putExtra(Constants.CREATED_ITEM_CATEGORY, info.getCategory());
                intent.putExtra(Constants.CREATED_ITEM_AMOUNT, info.getAmount());
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        initCategorySpinner();
        setupFloatingLabelError();
        buttonAdd.startAnimation(animation);
    }

    private CreatedItem addIncome() throws java.sql.SQLException {
        Date dateAdded;
        String incomeNote = description.getText().toString();
        double incomeAmount = Double.parseDouble(amount.getText().toString());
        String selectedAccount = categorySpin.getSelectedItem().toString();
        int currency = 1;
        try {
            dateAdded = dateFormat.parse(date.getText().toString());
        } catch (ParseException ex) {
            dateAdded = new Date();
        }

        Income income = new Income(incomeNote,selectedAccount, dateAdded, incomeAmount, currency);
        dbHelper = MoneyApplication.getInstance().GetDBHelper();
        dbHelper.getIncomeDAO().create(income);

        return new CreatedItem(income.getId(),selectedAccount,incomeAmount);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }

    private void initCategorySpinner() {
        final String[] categories = getResources().getStringArray(R.array.account_categories);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,categories);
        categorySpin.setAdapter(adapter);
    }

    private void setupFloatingLabelError() {
        String currencyName = CurrencyCache.getCurrencyOrEmpty().getName();
        String labelHint = String.format("%s, %s:", getString(R.string.amount), currencyName);
        floatingAmountLabel.setHint(labelHint);
        floatingAmountLabel.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {
                if (text.length() > 0) {
                    floatingAmountLabel.setErrorEnabled(false);
                } else {
                    floatingAmountLabel.setError(getString(R.string.outcome_amount_required));
                    floatingAmountLabel.setErrorEnabled(true);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 0) {
                    floatingAmountLabel.setError(getString(R.string.outcome_amount_required));
                    floatingAmountLabel.setErrorEnabled(true);
                }
            }
        });
    }

    public void showDatePickerDialog(View v) {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.setDate(date);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        private TextView date;

        public void setDate(TextView date) {
            this.date = date;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            month+=1;
            date.setText(day + "-" + month + "-" + year);
        }
    }
}
