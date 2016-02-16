package com.devmoroz.moneyme.utils.preference;

import android.os.Bundle;
import android.support.v7.preference.PreferenceDialogFragmentCompat;
import android.view.View;
import android.widget.NumberPicker;

import com.devmoroz.moneyme.R;


public class NumberPickerPreferenceDialog extends PreferenceDialogFragmentCompat {

    private static final String STATE_PICKER_VALUE = "number_picker_value";

    private NumberPicker numberPicker;

    private boolean restoredState;
    private int restoredValue;

    public static NumberPickerPreferenceDialog newInstance(String key) {
        NumberPickerPreferenceDialog fragment = new NumberPickerPreferenceDialog();
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
            restoredValue = savedInstanceState.getInt(STATE_PICKER_VALUE);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_PICKER_VALUE, numberPicker.getValue());
    }

    @Override
    protected void onBindDialogView(View view) {
        NumberPickerPreference preference = getNumberPickerPreference();

        numberPicker = (NumberPicker) view.findViewById(R.id.numberpicker);
        numberPicker.setMinValue(preference.getMinValue());
        numberPicker.setMaxValue(preference.getMaxValue());
        numberPicker.setValue(restoredState ? restoredValue : preference.getValue());
        numberPicker.setWrapSelectorWheel(preference.isWrapSelectorWheel());
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            int value = numberPicker.getValue();
            if (getNumberPickerPreference().callChangeListener(value)) {
                getNumberPickerPreference().setValue(value);
            }
        }
    }

    private NumberPickerPreference getNumberPickerPreference() {
        return (NumberPickerPreference) getPreference();
    }
}
