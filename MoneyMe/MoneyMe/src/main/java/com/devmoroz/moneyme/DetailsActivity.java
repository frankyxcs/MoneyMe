package com.devmoroz.moneyme;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
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
import com.devmoroz.moneyme.models.Category;
import com.devmoroz.moneyme.models.Currency;
import com.devmoroz.moneyme.models.Transaction;
import com.devmoroz.moneyme.models.TransactionType;
import com.devmoroz.moneyme.utils.Constants;
import com.devmoroz.moneyme.utils.CurrencyCache;
import com.devmoroz.moneyme.utils.CustomColorTemplate;
import com.devmoroz.moneyme.utils.FormatUtils;
import com.devmoroz.moneyme.utils.PhotoUtil;
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

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.Months;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
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
    private long mEarliestTransactionTimestamp;
    private long mLatestTransactionTimestamp;
    private static final String X_AXIS_PATTERN = "MMM YY";
    private static final int NO_DATA_COLOR = Color.GRAY;
    private boolean mChartDataPresent = true;
    Currency currency = CurrencyCache.getCurrencyOrEmpty();
    private EditText editAmountInput;
    private EditText editNotesInput;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        itemId = intent.getStringExtra(Constants.DETAILS_ITEM_ID);
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
            Transaction transaction = dbHelper.getTransactionDAO().queryForId(UUID.fromString(itemId));
            accountId = transaction.getAccount().getId();
            entityDate = transaction.getDateAdded();
            note = transaction.getNotes();
            amount = transaction.getAmount();
            if (itemType == TransactionType.INCOME) {
                collapsingToolbar.setTitle(getString(R.string.income_toolbar_name));
            } else {
                category = transaction.getCategory();
                collapsingToolbar.setTitle(category.getTitle());
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
        chart.setScaleEnabled(false);
        chart.setPinchZoom(true);
        chart.setDrawGridBackground(false);
        chart.setDescription("");
        chart.getLegend().setEnabled(false);
        chart.getAxisRight().setEnabled(false);

        LimitLine llXAxis = new LimitLine(10f, "Index 10");
        llXAxis.setLineWidth(4f);
        llXAxis.enableDashedLine(10f, 10f, 0f);
        llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        llXAxis.setTextSize(10f);

        chart.setData(getDataForChart(itemType));

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
            xAxis.setDrawGridLines(false);
            xAxis.setAvoidFirstLastClipping(true);
            chart.animateX(2500);
        }

        chart.invalidate();
    }

    private LineData getDataForChart(TransactionType type) {
        try {
            DBHelper dbHelper = MoneyApplication.getInstance().GetDBHelper();
            List<Transaction> transactions = Collections.emptyList();
            if (type == TransactionType.OUTCOME) {
                transactions = dbHelper.getTransactionDAO().queryTransactionsForCategory(category.getId());
            } else {
                transactions = dbHelper.getTransactionDAO().queryTransactionsByTypeForAccount(type, accountId);
            }
            if (!transactions.isEmpty()) {
                mChartDataPresent = true;
                mEarliestTransactionTimestamp = transactions.get(0).getDateLong();
                mLatestTransactionTimestamp = transactions.get(transactions.size() - 1).getDateLong();

                LocalDate startDate = new LocalDate(mEarliestTransactionTimestamp).withDayOfMonth(1);
                LocalDate endDate = new LocalDate(mLatestTransactionTimestamp).withDayOfMonth(1);
                int count = getDateDiff(new LocalDateTime(startDate.toDate().getTime()), new LocalDateTime(endDate.toDate().getTime()));

                List<String> xValues = new ArrayList<>();
                for (int i = 0; i <= count; i++) {
                    xValues.add(startDate.toString(X_AXIS_PATTERN));
                    startDate = startDate.plusMonths(1);
                }

                List<ILineDataSet> dataSets = new ArrayList<>();

                LocalDateTime earliest = new LocalDateTime(mEarliestTransactionTimestamp);
                LocalDateTime latest = new LocalDateTime(mLatestTransactionTimestamp);
                count = getDateDiff(earliest, latest);
                List<Entry> values = new ArrayList<>(count + 1);
                for (int i = 0; i <= count; i++) {
                    long start = 0;
                    long end = 0;

                    start = earliest.dayOfMonth().withMinimumValue().millisOfDay().withMinimumValue().toDate().getTime();
                    end = earliest.dayOfMonth().withMaximumValue().millisOfDay().withMaximumValue().toDate().getTime();

                    earliest = earliest.plusMonths(1);
                    float balance = 0f;
                    if (type == TransactionType.OUTCOME) {
                        balance = dbHelper.getTransactionDAO().getTotalSpendingForCategoryForPeriod(category.getId(), start, end);
                    } else {
                        balance = dbHelper.getTransactionDAO().getTotalIncomeForAccountForPeriod(accountId, start, end);
                    }
                    values.add(new Entry(balance, i));
                }
                LineDataSet set = new LineDataSet(values, "");
                WithCurrencyValueFormatter vFormatter = new WithCurrencyValueFormatter(currency.getSymbol());
                set.setValueFormatter(vFormatter);
                set.setDrawFilled(false);
                set.setLineWidth(4);

                set.setDrawCubic(true);
                if (type == TransactionType.OUTCOME) {
                    int col = category.getColor();
                    set.setColor(col);
                    set.setCircleColor(col);
                    set.setCircleColorHole(col);
                } else {
                    set.setColor(CustomColorTemplate.INCOME_COLOR);
                    set.setCircleColor(CustomColorTemplate.INCOME_COLOR);
                    set.setCircleColorHole(CustomColorTemplate.INCOME_COLOR);
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

    private int getDateDiff(LocalDateTime start, LocalDateTime end) {
        return Months.monthsBetween(start.withDayOfMonth(1).withMillisOfDay(0), end.withDayOfMonth(1).withMillisOfDay(0)).getMonths();
    }

    private LineData getEmptyData() {
        List<String> xValues = new ArrayList<>();
        List<Entry> yValues = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            xValues.add("");
            yValues.add(new Entry(i % 2 == 0 ? 5f : 4.5f, i));
        }
        List<ILineDataSet> dataSets = new ArrayList<>();
        LineDataSet set = new LineDataSet(yValues, "No chart data available");
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
                DBHelper dbHelper = MoneyApplication.getInstance().GetDBHelper();
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
