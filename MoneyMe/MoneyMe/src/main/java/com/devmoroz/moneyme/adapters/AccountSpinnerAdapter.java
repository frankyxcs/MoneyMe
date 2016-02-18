package com.devmoroz.moneyme.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.devmoroz.moneyme.models.Account;
import com.devmoroz.moneyme.models.Currency;
import com.devmoroz.moneyme.utils.FormatUtils;

import java.util.List;

public class AccountSpinnerAdapter extends ArrayAdapter<String> {

    List<Account> accounts;
    final Currency currency;

    public AccountSpinnerAdapter(Context context, int resource, String[] objects, List<Account> accounts, Currency currency) {
        super(context, resource, objects);
        this.accounts = accounts;
        this.currency = currency;
    }

    public Account getAccount(int position) {
        if (position <= accounts.size()) {
            return accounts.get(position);
        }
        return null;
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
        Account model = accounts.get(position);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View row = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
        TextView label = (TextView) row.findViewById(android.R.id.text1);
        label.setText(FormatUtils.attachAmountToText(model.getName(), currency, model.getBalance(), false));

        return row;
    }
}
