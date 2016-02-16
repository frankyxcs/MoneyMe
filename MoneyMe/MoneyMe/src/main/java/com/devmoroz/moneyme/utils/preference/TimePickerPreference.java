package com.devmoroz.moneyme.utils.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.preference.DialogPreference;
import android.util.AttributeSet;

import com.devmoroz.moneyme.R;
import com.devmoroz.moneyme.utils.datetime.TimeUtils;


public class TimePickerPreference extends DialogPreference {

    private String value;

    public TimePickerPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public TimePickerPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr,0);
    }

    public TimePickerPreference(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.timePickerPreferenceStyle);
    }

    public TimePickerPreference(Context context) {
        this(context, null);
    }

    public void setValue(String value) {
        this.value = value;
        int hour = TimeUtils.getHour(value);
        int minute = TimeUtils.getMinute(value);
        if(minute<10){
            setSummary(String.format("%s:0%s", String.valueOf(hour),String.valueOf(minute)));
        }else{
        setSummary(value);
        }
        persistString(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return(a.getString(index));
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        setValue(restorePersistedValue ? getPersistedString(value) : getPersistedString("18:00"));
    }
}
