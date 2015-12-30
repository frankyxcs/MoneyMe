package com.devmoroz.moneyme;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.devmoroz.moneyme.eventBus.BusProvider;
import com.devmoroz.moneyme.eventBus.WalletChangeEvent;
import com.devmoroz.moneyme.helpers.DBHelper;
import com.devmoroz.moneyme.models.Currency;
import com.devmoroz.moneyme.models.Income;
import com.devmoroz.moneyme.models.Outcome;
import com.devmoroz.moneyme.utils.Constants;
import com.devmoroz.moneyme.utils.CurrencyCache;
import com.devmoroz.moneyme.utils.CustomColorTemplate;
import com.devmoroz.moneyme.utils.FormatUtils;
import com.devmoroz.moneyme.utils.PhotoUtil;
import com.devmoroz.moneyme.utils.datetime.TimeUtils;
import com.devmoroz.moneyme.widgets.DecimalDigitsInputFilter;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

public class DetailsActivity extends AppCompatActivity {

    private CollapsingToolbarLayout collapsingToolbar;
    private FloatingActionButton fab;
    private Toolbar toolbar;
    private TextView amountTextView;
    private TextView dateTextView;
    private TextView notesTextView;
    private int itemId;
    private int itemType;
    private static Date entityDate = new Date();
    private static double amount = 0f;
    private static String note = "";
    private static String photoPath = "";
    Currency currency = CurrencyCache.getCurrencyOrEmpty();
    private EditText editAmountInput;
    private EditText editNotesInput;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        itemId = intent.getIntExtra(Constants.DETAILS_ITEM_ID, -1);
        itemType = intent.getIntExtra(Constants.DETAILS_ITEM_TYPE, -1);
        if(itemType == 1){
            setTheme(R.style.DetailsIncome);
        }else if (itemType == 2) {
            setTheme(R.style.DetailsOutcome);
        }
        else{
            setTheme(R.style.AppDefault);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        toolbar = (Toolbar) findViewById(R.id.details_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.details_collapsing_toolbar);
        amountTextView = (TextView) findViewById(R.id.details_amount);
        dateTextView = (TextView) findViewById(R.id.details_date);
        notesTextView = (TextView) findViewById(R.id.details_notes);

        loadEntity();
        setupFab();
        setupChart();

    }

    private void prepareIncomesData(int id) throws SQLException {
        DBHelper dbHelper = MoneyApplication.getInstance().GetDBHelper();
        Income current = dbHelper.getIncomeDAO().queryForId(id);
        entityDate = current.getDateOfReceipt();
        note = current.getNotes();
        amount = current.getAmount();
        collapsingToolbar.setTitle(getString(R.string.income_toolbar_name));

        amountTextView.setText(CurrencyCache.formatAmountWithSign(amount));
        dateTextView.setText(TimeUtils.formatHumanFriendlyShortDate(getApplicationContext(), entityDate.getTime()));
        if (FormatUtils.isNotEmpty(note)) {
            notesTextView.setText(note);
            notesTextView.setVisibility(View.VISIBLE);
        }
        else{
            notesTextView.setText(note);
            notesTextView.setVisibility(View.GONE);
        }
    }

    private void prepareOutcomesData(int id) throws SQLException {
        DBHelper dbHelper = MoneyApplication.getInstance().GetDBHelper();
        Outcome current = dbHelper.getOutcomeDAO().queryForId(id);
        entityDate = current.getDateOfSpending();
        note = current.getNotes();
        amount = current.getAmount();
        if (FormatUtils.isNotEmpty(current.getPhoto())) {
            loadImage(current.getPhoto());
        }
        collapsingToolbar.setTitle(current.getCategory());
        amountTextView.setText(CurrencyCache.formatAmountWithSign(amount));
        dateTextView.setText(TimeUtils.formatHumanFriendlyShortDate(getApplicationContext(), entityDate.getTime()));
        if (FormatUtils.isNotEmpty(note)) {
            notesTextView.setText(note);
            notesTextView.setVisibility(View.VISIBLE);
        }
        else{
            notesTextView.setText(note);
            notesTextView.setVisibility(View.GONE);
        }
    }

    private void loadEntity() {
        if (itemType != -1 && itemId != -1) {
            try {
                if (itemType == 1) {
                    prepareIncomesData(itemId);
                } else {
                    prepareOutcomesData(itemId);
                }
            } catch (SQLException ex) {

            }
        }
    }

    private void setupChart() {

    }

    private void setupFab() {
        fab = (FloatingActionButton) findViewById((R.id.fab_details_edit));

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog();
            }
        });
    }

    private void showEditDialog() {
        MaterialDialog dialog = new MaterialDialog.Builder(DetailsActivity.this)
                .title(R.string.edit)
                .customView(R.layout.dialog_edit, true)
                .negativeText(R.string.cancel)
                .positiveText(R.string.save)
                .positiveColorRes(R.color.colorPrimary)
                .negativeColorRes(R.color.colorPrimary)
                .widgetColorRes(R.color.colorPrimary)
                .alwaysCallInputCallback()
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                        String am = editAmountInput.getText().toString();
                        String no = editNotesInput.getText().toString();
                        updateEntity(am, no);
                    }
                })
                .build();

        final View positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
        final Button dateButton = (Button) dialog.getCustomView().findViewById(R.id.editEntryDate);
        dateButton.setText(TimeUtils.formatShortDate(getApplicationContext(), entityDate));
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment newFragment = new DatePickerFragment();
                newFragment.setDate(dateButton);
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });
        editAmountInput = (EditText) dialog.getCustomView().findViewById(R.id.editEntryAmount);
        editAmountInput.setText(String.valueOf(amount));
        editAmountInput.setFilters(new InputFilter[]{new DecimalDigitsInputFilter()});
        editAmountInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                positiveAction.setEnabled(s.toString().trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        editNotesInput = (EditText) dialog.getCustomView().findViewById(R.id.editEntryNote);
        editNotesInput.setText(note);

        dialog.show();
    }

    private void updateEntity(String amountString, String notes) {
        if (!(amountString.equals("0") || amountString.equals("0.") || amountString.equals("0.0") || amountString.equals("0.00"))) {
            double newAmount = Double.parseDouble(amountString);
            if (newAmount != amount || !note.equals(notes)) {
                DBHelper dbHelper = MoneyApplication.getInstance().GetDBHelper();
                try {
                    if (itemType == 1) {
                        Income in = dbHelper.getIncomeDAO().queryForId(itemId);
                        in.setAmount(newAmount);
                        in.setNotes(notes);
                        in.setDateOfReceipt(entityDate);
                        dbHelper.getIncomeDAO().update(in);
                    } else {
                        Outcome out = dbHelper.getOutcomeDAO().queryForId(itemId);
                        out.setAmount(newAmount);
                        out.setNotes(notes);
                        out.setDateOfSpending(entityDate);
                        dbHelper.getOutcomeDAO().update(out);
                    }
                    BusProvider.postOnMain(new WalletChangeEvent());
                } catch (SQLException ex) {

                }
            }
        }
    }

    private void loadImage(String path) {
        final ImageView imageView = (ImageView) findViewById(R.id.details_backdrop);
        PhotoUtil.setImageWithPicasso(getApplicationContext(), path, imageView);
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
            c.setTime(entityDate);
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, month);
            cal.set(Calendar.DAY_OF_MONTH, day);
            entityDate = cal.getTime();
            date.setText(TimeUtils.formatShortDate(getContext(), entityDate));
        }
    }
}
