package com.devmoroz.moneyme.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.devmoroz.moneyme.R;

public class AccountSpinnerWithIconsAdapter extends ArrayAdapter<String> {

    int[] typesOfAccountIcons = {R.drawable.ic_cash_multiple,R.drawable.ic_credit_card,R.drawable.ic_bank};
    String[] typesOfAccount;
    boolean isIntro;

    public AccountSpinnerWithIconsAdapter(Context context, int textViewResourceId,
                                          String[] objects, boolean isIntro) {
        super(context, textViewResourceId, objects);
        typesOfAccount = objects;
        this.isIntro = isIntro;
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(isIntro){
            return getClosedView(position,convertView,parent);
        }
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

    public View getClosedView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View row = inflater.inflate(R.layout.account_type_row_white, parent, false);
        TextView label = (TextView) row.findViewById(R.id.accountTypeText);
        label.setText(typesOfAccount[position]);

        ImageView icon = (ImageView) row.findViewById(R.id.accountTypeIcon);
        icon.setImageResource(typesOfAccountIcons[position]);

        return row;
    }
}
