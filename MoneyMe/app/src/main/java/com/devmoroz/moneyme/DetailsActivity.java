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
import com.devmoroz.moneyme.eventBus.BusProvider;
import com.devmoroz.moneyme.eventBus.WalletChangeEvent;
import com.devmoroz.moneyme.helpers.DBHelper;
import com.devmoroz.moneyme.models.Account;
import com.devmoroz.moneyme.models.Currency;
import com.devmoroz.moneyme.models.Transaction;
import com.devmoroz.moneyme.models.TransactionType;
import com.devmoroz.moneyme.utils.Constants;
import com.devmoroz.moneyme.utils.CurrencyCache;
import com.devmoroz.moneyme.utils.FormatUtils;
import com.devmoroz.moneyme.utils.PhotoUtil;
import com.devmoroz.moneyme.utils.datetime.TimeUtils;
import com.devmoroz.moneyme.widgets.DecimalDigitsInputFilter;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DetailsActivity extends AppCompatActivity {

    private CollapsingToolbarLayout collapsingToolbar;
    private FloatingActionButton fab;
    private Toolbar toolbar;
    private TextView amountTextView;
    private TextView dateTextView;
    private TextView notesTextView;
    private LineChart chart;
    private int itemId;
    private TransactionType itemType;
    private static Date entityDate = new Date();
    private static double amount = 0f;
    private static String note = "";
    private static String category = "";
    Currency currency = CurrencyCache.getCurrencyOrEmpty();
    private EditText editAmountInput;
    private EditText editNotesInput;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        itemId = intent.getIntExtra(Constants.DETAILS_ITEM_ID, -1);
        String type = intent.getStringExtra(Constants.DETAILS_ITEM_TYPE);
        itemType = TransactionType.valueOf(type);
        if (itemType == TransactionType.INCOME) {
            setTheme(R.style.DetailsIncome);
        } else if (itemType == TransactionType.OUTCOME) {
            setTheme(R.style.DetailsOutcome);
        } else {
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
        chart = (LineChart) findViewById(R.id.details_chart);

        loadEntity();
        setupFab();
        setupChart();

    }

    private void loadEntity() {
        try {
            DBHelper dbHelper = MoneyApplication.getInstance().GetDBHelper();
            Transaction transaction = dbHelper.getTransactionDAO().queryForId(itemId);
            entityDate = transaction.getDateAdded();
            note = transaction.getNotes();
            amount = transaction.getAmount();
            if (itemType == TransactionType.INCOME) {
                collapsingToolbar.setTitle(getString(R.string.income_toolbar_name));
            } else {
                category = transaction.getCategory();
                collapsingToolbar.setTitle(category);
                if (FormatUtils.isNotEmpty(transaction.getPhoto())) {
                    loadImage(transaction.getPhoto());
                }
            }
            amountTextView.setText(CurrencyCache.formatAmountWithSign(amount));
            dateTextView.setText(TimeUtils.formatHumanFriendlyShortDate(getApplicationContext(), entityDate.getTime()));
            if (FormatUtils.isNotEmpty(note)) {
                notesTextView.setText(note);
                notesTextView.setVisibility(View.VISIBLE);
            } else {
                notesTextView.setText(note);
                notesTextView.setVisibility(View.GONE);
            }
        } catch (SQLException ex) {

        }

    }

    private void setupChart() {
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setPinchZoom(true);
        chart.setDrawGridBackground(false);
        chart.setDescription("");
        chart.getXAxis().setDrawGridLines(false);
        chart.getAxisRight().setEnabled(false);

        LimitLine llXAxis = new LimitLine(10f, "Index 10");
        llXAxis.setLineWidth(4f);
        llXAxis.enableDashedLine(10f, 10f, 0f);
        llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        llXAxis.setTextSize(10f);

        HashMap<String, Float> dataMap = getDataForChart();

        ArrayList<String> xVals = new ArrayList<String>();

        ArrayList<Entry> yVals = new ArrayList<Entry>();

        if (!dataMap.isEmpty()) {
            int j = 0;
            Set<Map.Entry<String, Float>> set = dataMap.entrySet();
            for (Map.Entry<String, Float> entry : set) {
                xVals.add(entry.getKey());
                yVals.add(new Entry(entry.getValue(), j));
                j++;
            }
        }

        LineDataSet set1 = new LineDataSet(yVals, "");

        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(set1);
        LineData data = new LineData(xVals, dataSets);

        chart.setData(data);

        Legend l = chart.getLegend();
        l.setEnabled(true);
        l.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        l.setTextSize(14);
        l.setForm(Legend.LegendForm.CIRCLE);

        chart.getAxisLeft().setEnabled(true);
        chart.getAxisLeft().setStartAtZero(false);
        chart.getAxisLeft().removeAllLimitLines();

        chart.getXAxis().setEnabled(true);

        chart.animateX(2500);

        chart.invalidate();
    }

    private HashMap<String, Float> getDataForChart() {
        try {
            DBHelper dbHelper = MoneyApplication.getInstance().GetDBHelper();
            if (FormatUtils.isNotEmpty(category)) {
                List<Transaction> outcomes = dbHelper.getTransactionDAO().queryTransactionsForCategory(category);
                HashMap<String, Float> hashMap = new HashMap<>();
                for (Transaction o : outcomes) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(o.getDateAdded());
                    int month = cal.get(Calendar.MONTH) + 1;
                    int year = cal.get(Calendar.YEAR);
                    String key = String.valueOf(month) + "-" + String.valueOf(year);
                    float val = (float) o.getAmount();
                    if (hashMap.containsKey(key)) {
                        hashMap.put(key, hashMap.get(key) + val);
                    } else {
                        hashMap.put(key, val);
                    }
                }
                return hashMap;
            }
        } catch (SQLException ex) {

        }
        return new HashMap<>();
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
                    Transaction t = dbHelper.getTransactionDAO().queryForId(itemId);
                    t.setAmount(newAmount);
                    t.setNotes(notes);
                    t.setDateAdded(entityDate);
                    Account acc = dbHelper.getAccountDAO().queryForId(t.getAccount().getId());
                    if (itemType == TransactionType.INCOME) {
                        acc.setBalance(acc.getBalance() - amount);
                        acc.setBalance(acc.getBalance() + newAmount);
                    } else {
                        acc.setBalance(acc.getBalance() + amount);
                        acc.setBalance(acc.getBalance() - newAmount);
                    }
                    dbHelper.getTransactionDAO().update(t);
                    dbHelper.getAccountDAO().update(acc);

                    BusProvider.postOnMain(new WalletChangeEvent());
                } catch (SQLException ex) {

                }
            }
        }
    }

    private void loadImage(String path) {
        final ImageView imageView = (ImageView) findViewById(R.id.details_backdrop);
        PhotoUtil.setImageWithGlide(getApplicationContext(), path, imageView);
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
