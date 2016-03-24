package com.devmoroz.moneyme;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.devmoroz.moneyme.widgets.DateRangePickerDialogFragment;

import org.joda.time.LocalDate;

import java.util.Date;

public class ReportActivity extends AppCompatActivity implements DateRangePickerDialogFragment.OnDateRangeSetListener{

    private long mReportStartTime = new LocalDate().minusMonths(2).dayOfMonth().withMinimumValue().toDate().getTime();
    private long mReportEndTime = new LocalDate().plusDays(1).toDate().getTime();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDateRangeSet(Date startDate, Date endDate) {
        mReportStartTime = startDate.getTime();
        mReportEndTime = endDate.getTime();
    }
}
