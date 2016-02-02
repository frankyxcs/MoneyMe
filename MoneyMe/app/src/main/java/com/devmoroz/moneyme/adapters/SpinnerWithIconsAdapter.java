package com.devmoroz.moneyme.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.devmoroz.moneyme.R;

public class SpinnerWithIconsAdapter extends ArrayAdapter<String> {

    int[] typesOfAccountIcons = {R.drawable.ic_cash_multiple,R.drawable.ic_credit_card,R.drawable.ic_bank};
    String[] typesOfAccount;

    public SpinnerWithIconsAdapter(Context context, int textViewResourceId,
                                   String[] objects) {
        super(context, textViewResourceId, objects);
        typesOfAccount = objects;
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView,
                              ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View row = inflater.inflate(R.layout.account_type_row, parent, false);
        TextView label = (TextView) row.findViewById(R.id.accountTypeText);
        label.setText(typesOfAccount[position]);

        ImageView icon = (ImageView) row.findViewById(R.id.accountTypeIcon);
        icon.setImageResource(typesOfAccountIcons[position]);

        return row;
    }
}
