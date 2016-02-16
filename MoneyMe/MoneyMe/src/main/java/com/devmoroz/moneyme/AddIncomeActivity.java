package com.devmoroz.moneyme;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.devmoroz.moneyme.helpers.DBHelper;
import com.devmoroz.moneyme.logging.L;
import com.devmoroz.moneyme.models.Account;
import com.devmoroz.moneyme.models.CreatedItem;
import com.devmoroz.moneyme.models.Currency;
import com.devmoroz.moneyme.models.Transaction;
import com.devmoroz.moneyme.models.TransactionType;
import com.devmoroz.moneyme.utils.Constants;
import com.devmoroz.moneyme.utils.CurrencyCache;
import com.devmoroz.moneyme.utils.FormatUtils;
import com.devmoroz.moneyme.utils.datetime.TimeUtils;
import com.devmoroz.moneyme.widgets.DecimalDigitsInputFilter;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddIncomeActivity extends AppCompatActivity {

    private EditText amount;
    private EditText description;
    private TextInputLayout floatingAmountLabel;
    private Button date;
    private Spinner accountSpin;
    private static Date incomeDate = new Date();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppDefaultOutcome);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_income);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fab_grow);

        Toolbar toolbar = (Toolbar) findViewById(R.id.add_income_toolbar);
        if (toolbar != null) {
            toolbar.setTitle(R.string.income_toolbar_name);
            toolbar.setTitleTextColor(Color.WHITE);
            setSupportActionBar(toolbar);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        amount = (EditText) findViewById(R.id.add_income_amount);
        amount.setFilters(new InputFilter[]{new DecimalDigitsInputFilter()});
        description = (EditText) findViewById(R.id.add_income_note);
        date = (Button) findViewById(R.id.add_income_date);
        accountSpin = (Spinner) findViewById(R.id.add_income_category);
        date.setText(TimeUtils.formatShortDate(getApplicationContext(), new Date()));
        FloatingActionButton buttonAdd = (FloatingActionButton) findViewById(R.id.add_income_save);
        floatingAmountLabel = (TextInputLayout) findViewById(R.id.text_input_layout_income_amount);

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (amount.getText().toString().isEmpty()) {
                    floatingAmountLabel.setError(getString(R.string.outcome_amount_required));
                    floatingAmountLabel.setErrorEnabled(true);
                    return;
                }
                Intent intent;
                CreatedItem info = new CreatedItem("", "", 0, "");
                try {
                    info = addIncome();
                } catch (SQLException ex) {
                    L.t(AddIncomeActivity.this, "Something went wrong.Please,try again.");
                }
                intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra(Constants.CREATED_ITEM_ID, info.getItemId());
                intent.putExtra(Constants.CREATED_ITEM_CATEGORY, info.getCategory());
                intent.putExtra(Constants.CREATED_ITEM_AMOUNT, info.getAmount());
                intent.putExtra(Constants.CREATED_ITEM_ACCOUNT, info.getAccountId());
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        initAccountSpinner();
        setupFloatingLabelError();
        buttonAdd.startAnimation(animation);
    }

    private CreatedItem addIncome() throws java.sql.SQLException {
        Date dateAdded;
        String incomeNote = description.getText().toString();
        double incomeAmount = Double.parseDouble(amount.getText().toString());
        dateAdded = incomeDate == null ? new Date():incomeDate;


        DBHelper dbHelper = MoneyApplication.getInstance().GetDBHelper();
        int id = accountSpin.getSelectedItemPosition();
        Account account = dbHelper.getAccountDAO().queryForAll().get(id);
        account.setBalance(account.getBalance() + incomeAmount);

        Transaction income = new Transaction(TransactionType.INCOME,incomeNote,dateAdded, incomeAmount, account);

        dbHelper.getTransactionDAO().create(income);
        dbHelper.getAccountDAO().update(account);

        return new CreatedItem(income.getId(),account.getName(),incomeAmount, account.getId());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }

    private void initAccountSpinner() {
        List<Account> accountList = MoneyApplication.accounts;
        Currency c = CurrencyCache.getCurrencyOrEmpty();
        if(accountList!=null){
            String[] accountsWithBalance = new String[accountList.size()];
            for (int i=0;i<accountList.size();i++){
                Account acc = accountList.get(i);
                accountsWithBalance[i]= FormatUtils.attachAmountToText(acc.getName(), c, acc.getBalance(),false);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, accountsWithBalance);
            accountSpin.setAdapter(adapter);
        }
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

    public void showIncomeDatePickerDialog(View v) {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.setDate(date);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        private Button date;

        public void setDate(Button date) {
            this.date = date;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            incomeDate = c.getTime();

            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH,month);
            cal.set(Calendar.DAY_OF_MONTH, day);
            incomeDate = cal.getTime();
            date.setText(TimeUtils.formatShortDate(getContext(), incomeDate));
        }
    }
}
