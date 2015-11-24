package com.devmoroz.moneyme.utils.preference;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.devmoroz.moneyme.R;

public class LanguagePreference extends SpinnerPreference {

    private final LayoutInflater mLayoutInflater;

    public LanguagePreference(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LanguagePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mLayoutInflater = LayoutInflater.from(getContext());
    }

    @Override
    protected View createDropDownView(int position, ViewGroup parent) {
        return mLayoutInflater.inflate(R.layout.support_simple_spinner_dropdown_item, parent, false);
    }

    @Override
    protected void bindDropDownView(int position, View view) {
        ((TextView) view.findViewById(android.R.id.text1)).setText(mEntries[position]);
    }
}
