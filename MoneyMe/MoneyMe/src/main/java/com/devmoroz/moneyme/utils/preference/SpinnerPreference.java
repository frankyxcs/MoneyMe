package com.devmoroz.moneyme.utils.preference;


import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.devmoroz.moneyme.R;

public abstract class SpinnerPreference extends Preference {
    protected String[] mEntries = new String[0];
    protected String[] mEntryValues = new String[0];
    private int mInitialSelection = 0;

    public SpinnerPreference(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SpinnerPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWidgetLayoutResource(R.layout.preference_font_size);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SpinnerPreference);
        int entriesResId = ta.getResourceId(R.styleable.SpinnerPreference_entries, 0);
        if (entriesResId != 0) {
            mEntries = context.getResources().getStringArray(entriesResId);
        }
        int valuesResId = ta.getResourceId(R.styleable.SpinnerPreference_entryValues, 0);
        if (valuesResId != 0) {
            mEntryValues = context.getResources().getStringArray(valuesResId);
        }
        ta.recycle();
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        super.onSetInitialValue(restorePersistedValue, defaultValue);
        String value = restorePersistedValue ? getPersistedString(null) : (String) defaultValue;
        for (int i = 0; i < mEntryValues.length; i++) {
            if (TextUtils.equals(mEntryValues[i], value)) {
                mInitialSelection = i;
                break;
            }
        }
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        final Spinner spinner = (Spinner) holder.findViewById(R.id.spinner);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinner.performClick();
            }
        });
        spinner.setAdapter(new SpinnerAdapter() {
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = createDropDownView(position, parent);
                }
                bindDropDownView(position, convertView);
                return convertView;
            }

            @Override
            public void registerDataSetObserver(DataSetObserver observer) {
                // no op
            }

            @Override
            public void unregisterDataSetObserver(DataSetObserver observer) {
                // no op
            }

            @Override
            public int getCount() {
                return mEntries.length;
            }

            @Override
            public Object getItem(int position) {
                return null; // not applicable
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                return getDropDownView(position, convertView, parent);
            }

            @Override
            public int getItemViewType(int position) {
                return 0;
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }
        });
        spinner.setSelection(mInitialSelection);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                persistString(mEntryValues[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // no op
            }
        });
    }

    protected abstract View createDropDownView(int position, ViewGroup parent);

    protected abstract void bindDropDownView(int position, View view);
}
