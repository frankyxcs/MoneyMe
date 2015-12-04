package com.devmoroz.moneyme.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.devmoroz.moneyme.MoneyApplication;
import com.devmoroz.moneyme.R;
import com.devmoroz.moneyme.adapters.AccountsAdapter;
import com.devmoroz.moneyme.eventBus.BusProvider;
import com.devmoroz.moneyme.eventBus.WalletChangeEvent;
import com.devmoroz.moneyme.models.Account;
import com.devmoroz.moneyme.models.AccountRow;
import com.devmoroz.moneyme.models.Currency;
import com.devmoroz.moneyme.utils.CurrencyCache;
import com.devmoroz.moneyme.utils.FormatUtils;
import com.devmoroz.moneyme.widgets.DecimalDigitsInputFilter;
import com.devmoroz.moneyme.widgets.DividerItemDecoration;
import com.devmoroz.moneyme.widgets.EmptyRecyclerView;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class AccountsFragment extends Fragment {

    private View view;
    private EmptyRecyclerView recyclerView;
    private AccountsAdapter adapter;
    private TextView textTotalBalance;
    private ArrayList<AccountRow> data = new ArrayList<>();
    private List<Account> accounts;
    private double totalBalance = 0;
    private CardView btnAddAccount;

    private TextInputLayout accountNameInput;
    private View positiveAction;




    public AccountsFragment() {
    }

    public static AccountsFragment getInstance() {
        Bundle args = new Bundle();
        AccountsFragment fragment = new AccountsFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onPause() {
        BusProvider.getInstance().unregister(this);
        super.onPause();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.accounts_fragment, container, false);
        recyclerView = (EmptyRecyclerView) view.findViewById(R.id.accountsList);
        btnAddAccount = (CardView) view.findViewById(R.id.add_new_account);
        textTotalBalance = (TextView) view.findViewById(R.id.accountsTotalAmount);
        btnAddAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewAccount();
            }
        });
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        adapter = new AccountsAdapter(getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        CheckWallet();
        return view;
    }

    private void SetTotalBalance(){
        Currency currency = CurrencyCache.getCurrencyOrEmpty();
        String text = getString(R.string.total_amount);
        String value = FormatUtils.attachAmountToTextWithoutBrackets(text,currency, totalBalance,false);
        textTotalBalance.setText(value);
    }

    private void CheckWallet(){
        accounts = MoneyApplication.accounts;
        data.clear();
        totalBalance = 0;
        for (Account acc : accounts) {
            data.add(new AccountRow(acc.getId(),acc.getName(),acc.getBalance(),0,acc.getType()));
            totalBalance += acc.getBalance();
        }
        SetTotalBalance();
        adapter.setAccountsData(data);
    }

    private void AddNewAccount(){
        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title(R.string.add_account)
                .customView(R.layout.dialog_fragment_add_account, true)
                .theme(Theme.LIGHT)
                .negativeText(R.string.cancel)
                .positiveText(R.string.save)
                .positiveColorRes(R.color.colorPrimary)
                .negativeColorRes(R.color.colorPrimary)
                .widgetColorRes(R.color.colorPrimary)
                .alwaysCallInputCallback()
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                        View view = materialDialog.getCustomView();
                        CreateNewAccount(view);
                    }
                })
                .build();

        String[] types = getContext().getResources().getStringArray(R.array.account_types);
        positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
        accountNameInput = (TextInputLayout) dialog.getCustomView().findViewById(R.id.text_input_layout_add_account_name);
        EditText accountBalanceInput = (EditText) dialog.getCustomView().findViewById(R.id.accountAddBalance);
        Spinner accountTypesSpinner = (Spinner) dialog.getCustomView().findViewById(R.id.accountAddType);
        SpinnerWithIconsAdapter adapter = new SpinnerWithIconsAdapter(getContext(),R.layout.account_type_row,types);
        accountTypesSpinner.setAdapter(adapter);
        accountBalanceInput.setFilters(new InputFilter[]{new DecimalDigitsInputFilter()});
        accountNameInput.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                positiveAction.setEnabled(s.toString().trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        dialog.show();
        positiveAction.setEnabled(false);
    }

    private void CreateNewAccount(View view){

    }

    @Subscribe
    public void OnWalletChange(WalletChangeEvent event) {
       CheckWallet();
    }


    class SpinnerWithIconsAdapter extends ArrayAdapter<String> {

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
}
