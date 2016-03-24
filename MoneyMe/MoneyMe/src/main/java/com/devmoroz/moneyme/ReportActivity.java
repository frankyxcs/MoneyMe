package com.devmoroz.moneyme;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.devmoroz.moneyme.helpers.DBHelper;
import com.devmoroz.moneyme.widgets.DateRangePickerDialogFragment;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;

import org.joda.time.LocalDate;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReportActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,DateRangePickerDialogFragment.OnDateRangeSetListener{

    Spinner mTimeRangeSpinner;
    Spinner mAccountsSpinner;
    PieChart mPieChart;
    LineChart mLineChart;

    private long mReportStartTime = new LocalDate().minusMonths(2).dayOfMonth().withMinimumValue().toDate().getTime();
    private long mReportEndTime = new LocalDate().plusDays(1).toDate().getTime();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        mTimeRangeSpinner = (Spinner) findViewById(R.id.report_time_range_spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.report_time_range,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTimeRangeSpinner.setAdapter(adapter);
        mTimeRangeSpinner.setOnItemSelectedListener(this);
        mTimeRangeSpinner.setSelection(1);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.report_toolbar);
        if (toolbar != null) {
            toolbar.setTitle(R.string.report_toolbar_name);
            setSupportActionBar(toolbar);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onDateRangeSet(Date startDate, Date endDate) {
        mReportStartTime = startDate.getTime();
        mReportEndTime = endDate.getTime();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mReportEndTime = new LocalDate().plusDays(1).toDate().getTime();
        switch (position){
            case 0: //current month
                mReportStartTime = new LocalDate().dayOfMonth().withMinimumValue().toDate().getTime();
                break;
            case 1: // last 3 months.
                mReportStartTime = new LocalDate().minusMonths(2).dayOfMonth().withMinimumValue().toDate().getTime();
                break;
            case 2:// last 6 months.
                mReportStartTime = new LocalDate().minusMonths(5).dayOfMonth().withMinimumValue().toDate().getTime();
                break;
            case 3:// last year.
                mReportStartTime = new LocalDate().minusMonths(11).dayOfMonth().withMinimumValue().toDate().getTime();
                break;
            case 4: //ALL TIME
                mReportStartTime = -1;
                mReportEndTime = -1;
                break;
            case 5:
                DBHelper helper = MoneyApplication.GetDBHelper();
                long earliestTransactionTime = 0;
                try {
                    earliestTransactionTime = helper.getTransactionDAO().getTimestampOfEarliestTransaction();
                } catch (SQLException e) {
                }
                DialogFragment rangeFragment = DateRangePickerDialogFragment.newInstance(
                        earliestTransactionTime,
                        mReportEndTime,
                        this);
                rangeFragment.show(getSupportFragmentManager(), "range_dialog");
                break;
        }

        updateDateRange();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void updateDateRange(){

    }

    private void setupPieChart(){
        mPieChart.setCenterTextSize(18);
        mPieChart.setDescription("");
        mPieChart.getLegend().setForm(Legend.LegendForm.CIRCLE);
        mPieChart.getLegend().setWordWrapEnabled(true);
        mPieChart.getLegend().setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
    }

    private void displayPieChart() {
        mPieChart.highlightValues(null);
        mPieChart.clear();
    }

    private PieData getPieData() {
        PieDataSet dataSet = new PieDataSet(null, "");
        List<String> labels = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();

        dataSet.setColors(colors);
        dataSet.setSliceSpace(2f);
        return new PieData(labels, dataSet);
    }
}
