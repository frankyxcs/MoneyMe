package com.devmoroz.moneyme;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.devmoroz.moneyme.utils.FormatUtils;
import com.devmoroz.moneyme.utils.ThemeUtils;
import com.devmoroz.moneyme.utils.datetime.TimeUtils;
import com.devmoroz.moneyme.widgets.DecimalDigitsInputFilter;
import com.devmoroz.moneyme.widgets.TextBackgroundSpan;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;


public class AddTransferActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private static final int TAGS_REQUEST_CODE = 4123;

    private EditText amount;
    private EditText notes;
    private TextInputLayout floatingAmountLabel;
    private Button dateButton;
    private Button timeButton;
    private Button tagsButton;
    private Spinner accountFromSpin;
    private Spinner accountToSpin;
    private LinearLayout spinnersContainer;

    private static TransactionEdit transactionEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppDefaultTransaction);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transfer);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        if (savedInstanceState == null) {
            transactionEdit = new TransactionEdit();
            transactionEdit.setDate(new Date().getTime());
            transactionEdit.setTransactionType(TransactionType.TRANSFER);
        } else {
            transactionEdit = savedInstanceState.getParcelable(Constants.STATE_TRANSACTION_EDIT);
        }

        amount = (EditText) findViewById(R.id.add_transfer_amount);
        amount.setFilters(new InputFilter[]{new DecimalDigitsInputFilter()});
        notes = (EditText) findViewById(R.id.add_transfer_note);
        dateButton = (Button) findViewById(R.id.add_transfer_date);
        timeButton = (Button) findViewById(R.id.add_transfer_time);
        tagsButton = (Button) findViewById(R.id.add_transfer_tags);
        accountFromSpin = (Spinner) findViewById(R.id.add_transfer_from);
        accountToSpin = (Spinner) findViewById(R.id.add_transfer_to);
        dateButton.setText(TimeUtils.formatShortDate(getApplicationContext(), new Date(transactionEdit.getDate())));
        timeButton.setText(TimeUtils.formatShortTime(getApplicationContext(), new Date(transactionEdit.getDate())));
        floatingAmountLabel = (TextInputLayout) findViewById(R.id.text_input_layout_transfer_amount);
        spinnersContainer = (LinearLayout) findViewById(R.id.add_transfer_accounts_container);

        initToolbar();
        initAccountSpinners();
        setupFloatingLabel();
        initButtonListeners();
    }

    private void initButtonListeners() {
        dateButton.setOnClickListener(this);
        timeButton.setOnClickListener(this);
        tagsButton.setOnClickListener(this);
        tagsButton.setOnLongClickListener(this);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.add_transfer_toolbar);
        if (toolbar != null) {
            toolbar.setTitle(R.string.transfer_toolbar_name);
            setSupportActionBar(toolbar);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_transaction, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case (android.R.id.home):
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case (R.id.save_transaction_toolbar):
                saveNewTransferTransaction();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showDatePickerDialog() {
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

    public void showTimePickerDialog() {
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
    protected void onSaveInstanceState(Bundle outState) {
        if (transactionEdit != null) {
            outState.putParcelable(Constants.STATE_TRANSACTION_EDIT, transactionEdit);
        }
        super.onSaveInstanceState(outState);
    }

    private void initAccountSpinners() {
        List<Account> accountList = Collections.emptyList();
        try {
            DBHelper dbHelper = MoneyApplication.GetDBHelper();
            accountList = dbHelper.getAccountDAO().queryForAll();
            transactionEdit.setAccountTo(accountList.get(0));
            transactionEdit.setAccountFrom(accountList.get(0));
        } catch (SQLException ex) {

        }
        Currency c = CurrencyCache.getCurrencyOrEmpty();
        String[] res = new String[accountList.size()];
        AccountSpinnerAdapter adapterFrom = new AccountSpinnerAdapter(this, android.R.layout.simple_spinner_dropdown_item, res, accountList, c);
        AccountSpinnerAdapter adapterTo = new AccountSpinnerAdapter(this, android.R.layout.simple_spinner_dropdown_item, res, accountList, c);

        accountFromSpin.setAdapter(adapterFrom);
        accountFromSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                transactionEdit.setAccountFrom(adapterFrom.getAccount(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        accountToSpin.setAdapter(adapterTo);
        accountToSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                transactionEdit.setAccountTo(adapterTo.getAccount(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setupFloatingLabel() {
        String currencyName = CurrencyCache.getCurrencyOrEmpty().getName();
        String labelHint = String.format("%s, %s:", getString(R.string.amount), currencyName);
        floatingAmountLabel.setHint(labelHint);
    }

    private void saveNewTransferTransaction() {
        if (amount.getText().toString().isEmpty()) {
            Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
            floatingAmountLabel.startAnimation(shake);
            return;
        } else if (!(transactionEdit.validateAccountFrom() || transactionEdit.validateAccountTo())) {
            Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
            spinnersContainer.startAnimation(shake);
            return;
        }
        Intent intent;
        CreatedItem info = new CreatedItem("", "", 0, "");
        try {
            info = addTransfer();
        } catch (SQLException ex) {
            L.t(AddTransferActivity.this, "Something went wrong.Please,try again.");
        }
        intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra(Constants.CREATED_TRANSACTION_DETAILS, info);
        setResult(RESULT_OK, intent);
        finish();
    }

    private CreatedItem addTransfer() throws SQLException {
        DBHelper dbHelper = MoneyApplication.GetDBHelper();

        String transactionNote = notes.getText().toString();
        double transactionAmount = Double.parseDouble(amount.getText().toString());

        Account accountFrom = transactionEdit.getAccountFrom();
        accountFrom.setBalance(accountFrom.getBalance() - transactionAmount);
        Account accountTo = transactionEdit.getAccountTo();
        accountTo.setBalance(accountTo.getBalance() + transactionAmount);

        Transaction transfer = transactionEdit.getModel();
        transfer.setAmount(transactionAmount);
        transfer.setNotes(transactionNote);
        transfer.setSyncState(SyncState.None);

        dbHelper.getTransactionDAO().create(transfer);
        dbHelper.getAccountDAO().update(accountFrom);
        dbHelper.getAccountDAO().update(accountTo);

        return new CreatedItem(transfer.getId(), FormatUtils.getAccountTitle(transfer), transactionAmount, accountFrom.getId() + ";" + accountTo.getId());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_transfer_date:
                showDatePickerDialog();
                break;
            case R.id.add_transfer_tags:
                startTagsActivity(transactionEdit.getTags() != null ? transactionEdit.getTags() : Collections.<Tag>emptyList());
                break;
            case R.id.add_transfer_time:
                showTimePickerDialog();
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        switch(v.getId()){
            case R.id.add_transfer_tags:
                transactionEdit.setTags(null);
                tagsButton.setText(R.string.tags);
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
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
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
        final int tagBackgroundColor = ThemeUtils.getColor(AddTransferActivity.this, R.attr.backgroundColorSecondary);
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
