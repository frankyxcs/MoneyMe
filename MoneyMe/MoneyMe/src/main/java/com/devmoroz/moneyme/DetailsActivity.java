package com.devmoroz.moneyme;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.devmoroz.moneyme.eventBus.BusProvider;
import com.devmoroz.moneyme.eventBus.WalletChangeEvent;
import com.devmoroz.moneyme.helpers.DBHelper;
import com.devmoroz.moneyme.models.Account;
import com.devmoroz.moneyme.models.Category;
import com.devmoroz.moneyme.models.Currency;
import com.devmoroz.moneyme.models.Transaction;
import com.devmoroz.moneyme.models.TransactionEdit;
import com.devmoroz.moneyme.models.TransactionType;
import com.devmoroz.moneyme.utils.Constants;
import com.devmoroz.moneyme.utils.CurrencyCache;
import com.devmoroz.moneyme.utils.CustomColorTemplate;
import com.devmoroz.moneyme.utils.FormatUtils;
import com.devmoroz.moneyme.utils.PhotoUtil;
import com.devmoroz.moneyme.utils.datetime.DataInterval;
import com.devmoroz.moneyme.utils.datetime.TimeUtils;
import com.devmoroz.moneyme.widgets.DecimalDigitsInputFilter;
import com.devmoroz.moneyme.widgets.WithCurrencyValueFormatter;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.Months;
import org.joda.time.Period;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class DetailsActivity extends AppCompatActivity {

    private CollapsingToolbarLayout collapsingToolbar;
    private FloatingActionButton fab;
    private Toolbar toolbar;
    private TextView amountTextView;
    private TextView dateTextView;
    private TextView notesTextView;
    private LineChart chart;
    private String itemId;
    private TransactionType itemType;
    private static Date entityDate = new Date();
    private static double amount = 0f;
    private static String note = "";
    private static Category category;
    private static String accountId = "";
    private static final int NO_DATA_COLOR = Color.GRAY;
    private boolean mChartDataPresent = true;
    Currency currency = CurrencyCache.getCurrencyOrEmpty();
    private EditText editAmountInput;
    private EditText editNotesInput;
    private Spinner detailsPeriodSpinner;

    private DataInterval dInterval;
    private DataInterval.Type dataIntervalType = DataInterval.Type.WEEK;
    private int currentSelection = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppDefault);
        Intent intent = getIntent();
        itemId = intent.getStringExtra(Constants.DETAILS_ITEM_ID);
        String type = intent.getStringExtra(Constants.DETAILS_ITEM_TYPE);
        itemType = TransactionType.valueOf(type);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.details_collapsing_toolbar);
        amountTextView = (TextView) findViewById(R.id.details_amount);
        dateTextView = (TextView) findViewById(R.id.details_date);
        notesTextView = (TextView) findViewById(R.id.details_notes);
        chart = (LineChart) findViewById(R.id.details_chart);
        detailsPeriodSpinner = (Spinner) findViewById(R.id.detailsPeriodSpinner);

        dInterval = new DataInterval(this, dataIntervalType);

        initToolbar();
        loadEntity();
        setupFab();
        if (itemType == TransactionType.TRANSFER) {
            findViewById(R.id.chartContainer).setVisibility(View.GONE);
        } else {
            initSpinner();
            setupChart();
        }

    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.details_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void loadEntity() {
        try {
            DBHelper dbHelper = MoneyApplication.GetDBHelper();
            Transaction transaction = dbHelper.getTransactionDAO().queryForId(UUID.fromString(itemId));
            accountId = transaction.getAccountId();
            entityDate = transaction.getDateAdded();
            note = transaction.getNotes();
            amount = transaction.getAmount();
            if (itemType == TransactionType.INCOME) {
                collapsingToolbar.setTitle(getString(R.string.income_toolbar_name));
            } else if (itemType == TransactionType.OUTCOME) {
                category = transaction.getCategory();
                collapsingToolbar.setTitle(category.getTitle());
                if (FormatUtils.isNotEmpty(transaction.getPhoto())) {
                    loadImage(transaction.getPhoto());
                }
            } else {
                collapsingToolbar.setTitle(getString(R.string.transfer_toolbar_name));
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

    private void initSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.details_chart_period, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        detailsPeriodSpinner.setAdapter(adapter);
        detailsPeriodSpinner.setSelection(currentSelection);
        detailsPeriodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (currentSelection != position) {
                    currentSelection = position;
                    switch (position) {
                        case 0:
                            updateChart(DataInterval.Type.WEEK);
                            break;
                        case 1:
                            updateChart(DataInterval.Type.MONTH);
                            break;
                        case 2:
                            updateChart(DataInterval.Type.YEAR);
                            break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setupChart() {
        chart.setDragEnabled(true);
        chart.setScaleEnabled(false);
        chart.setPinchZoom(true);
        chart.setDrawGridBackground(false);
        chart.setDescription("");
        chart.getLegend().setEnabled(false);
        chart.getAxisRight().setEnabled(false);
        chart.setHighlightPerTapEnabled(false);
        chart.setHighlightPerDragEnabled(false);

        updateChart(dataIntervalType);
    }

    private void updateChart(DataInterval.Type type) {
        dInterval.setType(type);

        chart.setData(getDataForChart(itemType, dInterval.getInterval(), dInterval.getType()));

        if (!mChartDataPresent) {
            chart.getAxisLeft().setAxisMaxValue(10);
            chart.getAxisLeft().setDrawLabels(false);
            chart.getXAxis().setDrawLabels(false);
            chart.setTouchEnabled(false);
        } else {
            YAxis y = chart.getAxisLeft();
            y.setEnabled(false);
            y.setStartAtZero(false);
            y.setDrawGridLines(false);
            y.setDrawAxisLine(false);
            XAxis xAxis = chart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setTextSize(8f);
            xAxis.setDrawAxisLine(true);
            xAxis.setDrawGridLines(true);
            xAxis.setAvoidFirstLastClipping(true);
            chart.animateX(1000);
        }

        chart.invalidate();
    }

    private LineData getDataForChart(TransactionType type, Interval wholeInterval, DataInterval.Type intervalType) {
        try {
            Period period = getPeriod(intervalType);
            Interval firstInterval = new Interval(wholeInterval.getStart(), period);

            DBHelper dbHelper = MoneyApplication.GetDBHelper();

            List<Transaction> transactions;
            if (type == TransactionType.OUTCOME) {
                transactions = dbHelper.getTransactionDAO().queryTransactionsForCategory(category.getId());
            } else {
                transactions = dbHelper.getTransactionDAO().queryTransactionsByTypeForAccount(type, accountId);
            }
            if (!transactions.isEmpty()) {
                List<String> xValues = getXAxis(dInterval);

                List<ILineDataSet> dataSets = new ArrayList<>();

                int count = getDateDiff(intervalType);
                List<Entry> values = new ArrayList<>(count + 1);
                Interval interval = firstInterval;
                for (int i = 0; i <= count; i++) {
                    long start = 0;
                    long end = 0;

                    start = interval.getStartMillis();
                    end = interval.getEndMillis();

                    float balance = 0f;
                    if (type == TransactionType.OUTCOME) {
                        balance = dbHelper.getTransactionDAO().getTotalSpendingForCategoryForPeriod(category.getId(), start, end);
                    } else {
                        balance = dbHelper.getTransactionDAO().getTotalIncomeForAccountForPeriod(accountId, start, end);
                    }
                    values.add(new Entry(balance, i));
                    if (isNotAfterLastInterval(interval, wholeInterval) && values.size() < count) {
                        interval = getNextInterval(interval, period);
                    } else {
                        break;
                    }
                }
                if (!values.isEmpty()) {
                    mChartDataPresent = true;
                }
                LineDataSet set = new LineDataSet(values, "");
                WithCurrencyValueFormatter vFormatter = new WithCurrencyValueFormatter(currency.getSymbol());
                set.setValueFormatter(vFormatter);
                set.setDrawFilled(false);
                set.setLineWidth(3f);
                set.setDrawCubic(true);

                if (type == TransactionType.OUTCOME) {
                    int col = category.getColor();
                    set.setColor(col);
                    set.setDrawCircles(false);
                } else {
                    set.setColor(CustomColorTemplate.INCOME_COLOR);
                }
                dataSets.add(set);
                LineData lineData = new LineData(xValues, dataSets);

                return lineData;
            } else {
                return getEmptyData();
            }

        } catch (SQLException ex) {

        }
        return getEmptyData();
    }

    private List<String> getXAxis(DataInterval baseInterval) {
        final List<String> values = new ArrayList<>();
        final Period period = DataInterval.getSubPeriod(baseInterval.getType(), baseInterval.getLength());

        Interval interval = new Interval(baseInterval.getInterval().getStart(), period);
        while (interval.overlaps(baseInterval.getInterval())) {
            values.add(DataInterval.getSubTypeShortestTitle(interval, baseInterval.getType()));
            interval = new Interval(interval.getEnd(), period);
        }

        return values;
    }

    private Period getPeriod(DataInterval.Type intervalType) {
        switch (intervalType) {
            case WEEK:
            case MONTH:
                return Period.days(1);
            case YEAR:
                return Period.months(1);
            default:
                throw new IllegalArgumentException("Type " + intervalType + " is not supported.");
        }
    }

    private Interval getNextInterval(Interval interval, Period period) {
        return new Interval(interval.getEnd(), period);
    }

    private boolean isNotAfterLastInterval(Interval interval, Interval wholeInterval) {
        return interval.overlaps(wholeInterval);
    }

    private int getDateDiff(DataInterval.Type intervalType) {
        switch (intervalType) {
            case WEEK:
                return 7;
            case MONTH:
                Calendar c = Calendar.getInstance();
                return c.getActualMaximum(Calendar.DAY_OF_MONTH);
            case YEAR:
                return 12;
            default:
                throw new IllegalArgumentException("Type " + intervalType + " is not supported.");
        }
    }

    private LineData getEmptyData() {
        List<String> xValues = new ArrayList<>();
        List<Entry> yValues = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            xValues.add("");
            yValues.add(new Entry(i % 2 == 0 ? 5f : 4.5f, i));
        }
        List<ILineDataSet> dataSets = new ArrayList<>();
        LineDataSet set = new LineDataSet(yValues, getResources().getString(R.string.chart_no_data));
        set.setDrawFilled(true);
        set.setDrawValues(false);
        set.setColor(NO_DATA_COLOR);
        set.setFillColor(NO_DATA_COLOR);
        mChartDataPresent = false;
        dataSets.add(set);
        return new LineData(xValues, dataSets);
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
                DBHelper dbHelper = MoneyApplication.GetDBHelper();
                try {
                    Transaction t = dbHelper.getTransactionDAO().queryForId(UUID.fromString(itemId));
                    t.setAmount(newAmount);
                    t.setNotes(notes);
                    t.setDateAdded(entityDate);
                    Account acc = t.getAccount();
                    if (itemType == TransactionType.INCOME) {
                        acc.setBalance(acc.getBalance() - amount);
                        acc.setBalance(acc.getBalance() + newAmount);
                    } else {
                        acc.setBalance(acc.getBalance() + amount);
                        acc.setBalance(acc.getBalance() - newAmount);
                    }
                    dbHelper.getTransactionDAO().update(t);
                    dbHelper.getAccountDAO().update(acc);

                    amount = newAmount;
                    note = notes;

                    amountTextView.setText(CurrencyCache.formatAmountWithSign(amount));
                    dateTextView.setText(TimeUtils.formatHumanFriendlyShortDate(getApplicationContext(), entityDate.getTime()));
                    if (FormatUtils.isNotEmpty(note)) {
                        notesTextView.setText(note);
                        notesTextView.setVisibility(View.VISIBLE);
                    } else {
                        notesTextView.setText(note);
                        notesTextView.setVisibility(View.GONE);
                    }

                    BusProvider.postOnMain(new WalletChangeEvent());
                } catch (SQLException ex) {

                }
            }
        }
    }

    private void loadImage(String path) {
        final ImageView imageView = (ImageView) findViewById(R.id.details_backdrop);
        PhotoUtil.setImageWithGlide(getApplicationContext(), Uri.fromFile(new File(path)), imageView);
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
