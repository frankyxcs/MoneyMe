package com.devmoroz.moneyme.fragments.custom;


import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.Button;
import android.widget.TimePicker;

import com.devmoroz.moneyme.utils.datetime.TimeUtils;

import java.util.Calendar;
import java.util.Date;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    private Button timeButton;
    private Date selectedTime;
    private Callback listener;

    public interface Callback {
        void onTimeSet(Date date);
    }

    public void setCallback(Callback listener) {
        this.listener = listener;
    }

    public void setTimeButton(Button timeButton) {
        this.timeButton = timeButton;
    }

    public void setTime(Date time) {
        this.selectedTime = time;
    }

    public void setTime(long time) {
        this.selectedTime = new Date(time);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Calendar c = Calendar.getInstance();
        c.setTime(selectedTime);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(selectedTime);
        cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
        cal.set(Calendar.MINUTE, minute);
        timeButton.setText(TimeUtils.formatShortTime(timeButton.getContext(), cal.getTime()));
        listener.onTimeSet(cal.getTime());
    }
}
