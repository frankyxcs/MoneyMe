package com.devmoroz.moneyme.fragments;

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
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.devmoroz.moneyme.MainActivity;
import com.devmoroz.moneyme.MoneyApplication;
import com.devmoroz.moneyme.R;
import com.devmoroz.moneyme.adapters.AccountsAdapter;
import com.devmoroz.moneyme.adapters.AccountSpinnerWithIconsAdapter;
import com.devmoroz.moneyme.eventBus.BusProvider;
import com.devmoroz.moneyme.eventBus.WalletChangeEvent;
import com.devmoroz.moneyme.helpers.DBHelper;
import com.devmoroz.moneyme.models.Account;
import com.devmoroz.moneyme.models.AccountRow;
import com.devmoroz.moneyme.models.Currency;
import com.devmoroz.moneyme.utils.Constants;
import com.devmoroz.moneyme.utils.CurrencyCache;
import com.devmoroz.moneyme.utils.FormatUtils;
import com.devmoroz.moneyme.widgets.DecimalDigitsInputFilter;
import com.devmoroz.moneyme.widgets.DividerItemDecoration;
import com.devmoroz.moneyme.widgets.EmptyRecyclerView;
import com.squareup.otto.Subscribe;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AccountsFragment extends Fragment implements View.OnClickListener {

    private View view;
    private EmptyRecyclerView recyclerView;
    private AccountsAdapter adapter;
    private TextView balanceTextView;
    private ArrayList<AccountRow> data = new ArrayList<>();
    private List<Account> accounts;
    private double totalBalance = 0;
    private CardView btnAddAccount;
    private View transferContainerView;

    private TextInputLayout accountNameInput;
    private View positiveAction;

    private MainActivity mainActivity;

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
        mainActivity = (MainActivity) getActivity();

        view = inflater.inflate(R.layout.accounts_fragment, container, false);
        recyclerView = (EmptyRecyclerView) view.findViewById(R.id.accountsList);
        btnAddAccount = (CardView) view.findViewById(R.id.add_new_account);
        balanceTextView = (TextView) view.findViewById(R.id.balanceTextView);
        transferContainerView = view.findViewById(R.id.transferContainerView);
        transferContainerView.setOnClickListener(this);
        btnAddAccount.setOnClickListener(this);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        adapter = new AccountsAdapter(getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        CheckWallet();
        return view;
    }

    private void SetTotalBalance() {
        Currency currency = CurrencyCache.getCurrencyOrEmpty();
        String value = FormatUtils.amountToString(currency, totalBalance, true);
        balanceTextView.setText(value);

    }

    private void CheckWallet() {
        accounts = MoneyApplication.getInstance().getAccounts();
        if (accounts.size() >= 2) {
            transferContainerView.setVisibility(View.VISIBLE);
            transferContainerView.setOnClickListener(this);
        } else {
            transferContainerView.setVisibility(View.GONE);
        }
        DBHelper dbHelper = MoneyApplication.GetDBHelper();
        data.clear();
        totalBalance = 0;
        for (Account acc : accounts) {
            double spendings = 0;
            try {
                spendings = dbHelper.getTransactionDAO().getTotalOutcomeForAccount(acc.getId());
            } catch (SQLException ex) {

            }
            data.add(new AccountRow(acc.getId(), acc.getName(), acc.getBalance(), spendings, acc.getType()));
            totalBalance += acc.getBalance();
        }
        SetTotalBalance();
        adapter.setAccountsData(data);
    }

    private void AddNewAccount() {
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
                        CreateNewAccount(materialDialog);
                    }
                })
                .build();

        String[] types = getContext().getResources().getStringArray(R.array.account_types);
        positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
        accountNameInput = (TextInputLayout) dialog.getCustomView().findViewById(R.id.text_input_layout_add_account_name);
        EditText accountBalanceInput = (EditText) dialog.getCustomView().findViewById(R.id.accountAddBalance);
        Spinner accountTypesSpinner = (Spinner) dialog.getCustomView().findViewById(R.id.accountAddType);
        AccountSpinnerWithIconsAdapter adapter = new AccountSpinnerWithIconsAdapter(getContext(), R.layout.account_type_row, types,false);
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

    private void CreateNewAccount(MaterialDialog materialDialog) {
        View v = materialDialog.getCustomView();

        EditText nameEditText = (EditText) v.findViewById(R.id.accountAddName);
        EditText balanceEditText = (EditText) v.findViewById(R.id.accountAddBalance);
        Spinner typeSpinner = (Spinner) v.findViewById(R.id.accountAddType);

        String name = nameEditText.getText().toString();
        int type = typeSpinner.getSelectedItemPosition();
        double amount = 0f;
        if (!FormatUtils.isEmpty(balanceEditText)) {
            amount = Double.parseDouble(balanceEditText.getText().toString());
        }
        try {
            DBHelper dbHelper = MoneyApplication.GetDBHelper();
            Account account = new Account(name, amount, type, true);
            dbHelper.getAccountDAO().create(account);
        } catch (SQLException ex) {

        }
        BusProvider.postOnMain(new WalletChangeEvent());
        materialDialog.dismiss();
    }

    @Subscribe
    public void OnWalletChange(WalletChangeEvent event) {
        CheckWallet();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.transferContainerView:
                if (mainActivity != null) {
                    mainActivity.startAddActivity(Constants.TRANSFER_ACTIVITY, null);
                }
                break;
            case R.id.add_new_account:
                AddNewAccount();
                break;
        }
    }
}
