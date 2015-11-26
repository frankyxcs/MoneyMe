package com.devmoroz.moneyme.utils.preference;


import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.preference.DialogPreference;
import android.util.AttributeSet;

import com.devmoroz.moneyme.R;

public class NumberPickerPreference extends DialogPreference {

    private int minValue;
    private int maxValue;
    private boolean wrapSelectorWheel;
    private String summaryText;


    private int value;

    public NumberPickerPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        summaryText = context.getString(R.string.number);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.NumberPickerPreference, defStyleAttr, defStyleRes);
        minValue = typedArray.getInt(R.styleable.NumberPickerPreference_minValue, 1);
        maxValue = typedArray.getInt(R.styleable.NumberPickerPreference_maxValue, 28);
        wrapSelectorWheel = typedArray.getBoolean(R.styleable.NumberPickerPreference_wrapSelectorWheel, true);
        typedArray.recycle();
    }

    public NumberPickerPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public NumberPickerPreference(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.numberPickerPreferenceStyle);
    }

    public NumberPickerPreference(Context context) {
        this(context, null);
    }

    public int getMinValue() {
        return minValue;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public boolean isWrapSelectorWheel() {
        return wrapSelectorWheel;
    }

    public void setValue(int value) {
        this.value = value;
        setSummary(String.format("%s %s",Integer.toString(value),summaryText));
        persistInt(value);
    }

    public int getValue() {
        return value;
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, minValue > 0 ? minValue : 0);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        setValue(restorePersistedValue ? getPersistedInt(value) : (Integer) defaultValue);
    }

}
