package com.devmoroz.moneyme.widgets;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.devmoroz.moneyme.R;
import com.squareup.timessquare.CalendarPickerView;

import org.joda.time.LocalDate;

import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class DateRangePickerDialogFragment extends DialogFragment {

    CalendarPickerView mCalendarPickerView;
    Button mDoneButton;
    Button mCancelButton;

    private Date mStartRange = LocalDate.now().minusMonths(1).toDate();
    private Date mEndRange = LocalDate.now().toDate();
    private OnDateRangeSetListener mDateRangeSetListener;

    public static DateRangePickerDialogFragment newInstance(OnDateRangeSetListener dateRangeSetListener){
        DateRangePickerDialogFragment fragment = new DateRangePickerDialogFragment();
        fragment.mDateRangeSetListener = dateRangeSetListener;
        return fragment;
    }

    public static DateRangePickerDialogFragment newInstance(long startDate, long endDate,
                                                            OnDateRangeSetListener dateRangeSetListener){
        DateRangePickerDialogFragment fragment = new DateRangePickerDialogFragment();
        fragment.mStartRange = new Date(startDate);
        fragment.mEndRange = new Date(endDate);
        fragment.mDateRangeSetListener = dateRangeSetListener;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_daterange_picker, container, false);
        mDoneButton = (Button) view.findViewById(R.id.saveButton);
        mCancelButton = (Button) view.findViewById(R.id.cancelButton);
        mCalendarPickerView = (CalendarPickerView) view.findViewById(R.id.calendar_view);


        Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 1);

        Date today = new Date();
        mCalendarPickerView.init(mStartRange, mEndRange)
                .inMode(CalendarPickerView.SelectionMode.RANGE)
                .withSelectedDate(today);

        mDoneButton.setText(R.string.confirm);
        mDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Date> selectedDates = mCalendarPickerView.getSelectedDates();
                Date startDate = selectedDates.get(0);
                Date endDate = selectedDates.size() == 2 ? selectedDates.get(1) : new Date();
                mDateRangeSetListener.onDateRangeSet(startDate, endDate);
                dismiss();
            }
        });

        mCancelButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return view;
    }

    public interface OnDateRangeSetListener {
        void onDateRangeSet(Date startDate, Date endDate);
    }
}
