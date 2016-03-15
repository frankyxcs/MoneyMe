package com.devmoroz.moneyme;


import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.devmoroz.moneyme.adapters.AccountSpinnerAdapter;
import com.devmoroz.moneyme.fragments.custom.DatePickerFragment;
import com.devmoroz.moneyme.fragments.custom.TimePickerFragment;
import com.devmoroz.moneyme.helpers.DBHelper;
import com.devmoroz.moneyme.logging.L;
import com.devmoroz.moneyme.models.Account;
import com.devmoroz.moneyme.models.CreatedItem;
import com.devmoroz.moneyme.models.Currency;
import com.devmoroz.moneyme.models.SyncState;
import com.devmoroz.moneyme.models.Tag;
import com.devmoroz.moneyme.models.Transaction;
import com.devmoroz.moneyme.models.TransactionEdit;
import com.devmoroz.moneyme.models.TransactionType;
import com.devmoroz.moneyme.utils.Constants;
import com.devmoroz.moneyme.utils.CurrencyCache;
import com.devmoroz.moneyme.utils.ThemeUtils;
import com.devmoroz.moneyme.utils.datetime.TimeUtils;
import com.devmoroz.moneyme.widgets.DecimalDigitsInputFilter;
import com.devmoroz.moneyme.widgets.TextBackgroundSpan;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class AddIncomeActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener{

    private static final int TAGS_REQUEST_CODE = 3123;

    private EditText amount;
    private EditText description;
    private TextInputLayout floatingAmountLabel;
    private Button dateButton;
    private Button timeButton;
    private Button tagsButton;
    private Spinner accountSpin;

    private static TransactionEdit transactionEdit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppDefaultTransaction);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_income);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        if (savedInstanceState == null) {
            transactionEdit = new TransactionEdit();
            transactionEdit.setDate(new Date().getTime());
            transactionEdit.setTransactionType(TransactionType.INCOME);
        } else {
            transactionEdit = savedInstanceState.getParcelable(Constants.STATE_TRANSACTION_EDIT);
        }


        amount = (EditText) findViewById(R.id.add_income_amount);
        amount.setFilters(new InputFilter[]{new DecimalDigitsInputFilter()});
        description = (EditText) findViewById(R.id.add_income_note);
        dateButton = (Button) findViewById(R.id.add_income_date);
        timeButton = (Button) findViewById(R.id.add_income_time);
        tagsButton = (Button) findViewById(R.id.add_income_tags);
        accountSpin = (Spinner) findViewById(R.id.add_income_category);
        dateButton.setText(TimeUtils.formatShortDate(getApplicationContext(), new Date(transactionEdit.getDate())));
        timeButton.setText(TimeUtils.formatShortTime(getApplicationContext(), new Date(transactionEdit.getDate())));
        floatingAmountLabel = (TextInputLayout) findViewById(R.id.text_input_layout_income_amount);

        initButtonListeners();
        initToolbar();
        initAccountSpinner();
        setupFloatingLabelError();
    }

    private void initButtonListeners() {
        dateButton.setOnClickListener(this);
        timeButton.setOnClickListener(this);
        tagsButton.setOnClickListener(this);
        tagsButton.setOnLongClickListener(this);
    }

    private void saveNewIncomeTransaction() {
        if (amount.getText().toString().isEmpty()) {
            Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
            floatingAmountLabel.startAnimation(shake);
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
        intent.putExtra(Constants.CREATED_TRANSACTION_DETAILS, info);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (transactionEdit != null) {
            outState.putParcelable(Constants.STATE_TRANSACTION_EDIT, transactionEdit);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_transaction, menu);
        return true;
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.add_income_toolbar);
        if (toolbar != null) {
            toolbar.setTitle(R.string.income_toolbar_name);
            setSupportActionBar(toolbar);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private CreatedItem addIncome() throws java.sql.SQLException {
        DBHelper dbHelper = MoneyApplication.GetDBHelper();

        String incomeNote = description.getText().toString();
        double incomeAmount = Double.parseDouble(amount.getText().toString());

        Account account = transactionEdit.getAccountTo();
        account.setBalance(account.getBalance() + incomeAmount);

        Transaction income = transactionEdit.getModel();
        income.setAmount(incomeAmount);
        income.setNotes(incomeNote);
        income.setSyncState(SyncState.None);

        dbHelper.getTransactionDAO().create(income);
        dbHelper.getAccountDAO().update(account);

        return new CreatedItem(income.getId(),account.getName(),incomeAmount, account.getId());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case (android.R.id.home):
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case (R.id.save_transaction_toolbar):
                saveNewIncomeTransaction();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initAccountSpinner() {
        List<Account> accountList = Collections.emptyList();
        try {
            DBHelper dbHelper = MoneyApplication.GetDBHelper();
            accountList = dbHelper.getAccountDAO().queryForAll();
            transactionEdit.setAccountTo(accountList.get(0));
        } catch (SQLException ex) {

        }
        Currency c = CurrencyCache.getCurrencyOrEmpty();
        String[] res = new String[accountList.size()];
        AccountSpinnerAdapter adapter = new AccountSpinnerAdapter(this, android.R.layout.simple_spinner_dropdown_item , res,accountList, c);
        accountSpin.setAdapter(adapter);
        accountSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                transactionEdit.setAccountTo(adapter.getAccount(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setupFloatingLabelError() {
        String currencyName = CurrencyCache.getCurrencyOrEmpty().getName();
        String labelHint = String.format("%s, %s:", getString(R.string.amount), currencyName);
        floatingAmountLabel.setHint(labelHint);
    }

    public void showIncomeDatePickerDialog() {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.setDateButton(dateButton);
        newFragment.setCallback(new DatePickerFragment.Callback() {
            @Override
            public void onDateSet(Date date) {
                transactionEdit.setDate(date.getTime());
            }
        });
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void showIncomeTimePickerDialog() {
        TimePickerFragment newFragment = new TimePickerFragment();
        newFragment.setTimeButton(timeButton);
        newFragment.setCallback(new TimePickerFragment.Callback() {
            @Override
            public void onTimeSet(Date date) {
                transactionEdit.setDate(date.getTime());
            }
        });
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_income_date:
                showIncomeDatePickerDialog();
                break;
            case R.id.add_income_tags:
                startTagsActivity(transactionEdit.getTags() != null ? transactionEdit.getTags() : Collections.<Tag>emptyList());
                break;
            case R.id.add_income_time:
                showIncomeTimePickerDialog();
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        switch(v.getId()){
            case R.id.add_income_tags:
                transactionEdit.setTags(null);
                tagsButton.setText(R.string.add_tags);
                return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case TAGS_REQUEST_CODE:
                    transactionEdit.setTags(getTagsExtra(data));
                    setTags(transactionEdit.getTags());
                    break;
            }
        }
    }

    private void startTagsActivity(List<Tag> selectedTags) {
        Intent intent = new Intent(this, TagsActivity.class);
        final Parcelable[] parcelables = new Parcelable[selectedTags.size()];
        int index = 0;
        for (Tag tag : selectedTags) {
            parcelables[index++] = tag;
        }
        intent.putExtra(Constants.EXTRA_SELECTED_TAGS, parcelables);
        startActivityForResult(intent, TAGS_REQUEST_CODE);
        overridePendingTransition(R.anim.activity_open_translate,R.anim.activity_close_scale);
    }

    public static List<Tag> getTagsExtra(Intent data) {
        final Parcelable[] parcelables = data.getParcelableArrayExtra(Constants.RESULT_EXTRA_TAGS);
        final List<Tag> tags = new ArrayList<>();
        for (Parcelable parcelable : parcelables) {
            tags.add((Tag) parcelable);
        }
        return tags;
    }

    private void setTags(List<Tag> tags) {
        final int tagBackgroundColor = ThemeUtils.getColor(AddIncomeActivity.this, R.attr.backgroundColorSecondary);
        final float tagBackgroundRadius = getResources().getDimension(R.dimen.tag_radius);
        if (tags == null) {
            tags = Collections.emptyList();
        }
        final SpannableStringBuilder ssb = new SpannableStringBuilder();
        for (Tag tag : tags) {
            ssb.append(tag.getTitle());
            ssb.setSpan(new TextBackgroundSpan(tagBackgroundColor, tagBackgroundRadius), ssb.length() - tag.getTitle().length(), ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssb.append(" ");
        }
        tagsButton.setText(ssb);
    }

}
