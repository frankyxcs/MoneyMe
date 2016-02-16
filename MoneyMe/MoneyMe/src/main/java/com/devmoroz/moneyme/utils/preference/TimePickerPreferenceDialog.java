package com.devmoroz.moneyme.utils.preference;

import android.os.Bundle;
import android.support.v7.preference.PreferenceDialogFragmentCompat;
import android.view.View;
import android.widget.TimePicker;

import com.devmoroz.moneyme.R;
import com.devmoroz.moneyme.utils.datetime.TimeUtils;


public class TimePickerPreferenceDialog extends PreferenceDialogFragmentCompat {

    private static final String STATE_PICKER_VALUE = "time_picker_value";

    private TimePicker timePicker;

    private boolean restoredState;
    private String restoredValue;

    private int lastHour=18;
    private int lastMinute=0;


    public static TimePickerPreferenceDialog newInstance(String key) {
        TimePickerPreferenceDialog fragment = new TimePickerPreferenceDialog();
        Bundle args = new Bundle(1);
        args.putString(ARG_KEY, key);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            restoredState = true;
            restoredValue = savedInstanceState.getString(STATE_PICKER_VALUE);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        int lastHour = timePicker.getCurrentHour();
        int lastMinute = timePicker.getCurrentMinute();

        String time = String.valueOf(lastHour)+":"+String.valueOf(lastMinute);
        outState.putString(STATE_PICKER_VALUE, time);
    }

    @Override
    protected void onBindDialogView(View view) {
        TimePickerPreference preference = getTimePickerPreference();

        timePicker = (TimePicker) view.findViewById(R.id.timepicker);
        lastHour = restoredState ? TimeUtils.getHour(restoredValue) : TimeUtils.getHour(preference.getValue());
        lastMinute = restoredState ? TimeUtils.getMinute(restoredValue) : TimeUtils.getMinute(preference.getValue());
        timePicker.setCurrentHour(lastHour);
        timePicker.setCurrentMinute(lastMinute);
    }


    @Override
    public void onDialogClosed(boolean b) {
        if (b) {
            int lastHour=timePicker.getCurrentHour();
            int lastMinute=timePicker.getCurrentMinute();

            String time = String.valueOf(lastHour)+":"+String.valueOf(lastMinute);
            if (getTimePickerPreference().callChangeListener(time)) {
                getTimePickerPreference().setValue(time);
            }
        }
    }

    private TimePickerPreference getTimePickerPreference() {
        return (TimePickerPreference) getPreference();
    }
}
