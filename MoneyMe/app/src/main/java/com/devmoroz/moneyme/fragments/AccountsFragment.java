package com.devmoroz.moneyme.fragments;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.afollestad.materialdialogs.internal.MDTintHelper;
import com.afollestad.materialdialogs.internal.ThemeSingleton;
import com.devmoroz.moneyme.MoneyApplication;
import com.devmoroz.moneyme.R;
import com.devmoroz.moneyme.adapters.AccountsAdapter;
import com.devmoroz.moneyme.eventBus.BusProvider;
import com.devmoroz.moneyme.eventBus.WalletChangeEvent;
import com.devmoroz.moneyme.models.Account;
import com.devmoroz.moneyme.models.AccountRow;
import com.devmoroz.moneyme.widgets.DividerItemDecoration;
import com.devmoroz.moneyme.widgets.EmptyRecyclerView;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class AccountsFragment extends Fragment {

    private View view;
    private EmptyRecyclerView recyclerView;
    private AccountsAdapter adapter;
    private ArrayList<AccountRow> data = new ArrayList<>();
    private List<Account> accounts;
    private Button btnAddAccount;

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
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        accounts = MoneyApplication.getInstance().accounts;
        data.clear();
        for (Account acc : accounts) {
            data.add(new AccountRow(acc.getId(),acc.getName(),acc.getAmount(),0));
        }
        view = inflater.inflate(R.layout.accounts_fragment, container, false);
        recyclerView = (EmptyRecyclerView) view.findViewById(R.id.accountsList);
        btnAddAccount = (Button) view.findViewById(R.id.add_new_account);
        btnAddAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewAccount();
            }
        });
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        adapter = new AccountsAdapter(getActivity(), data);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }

    private void CheckWallet(){
        accounts = MoneyApplication.getInstance().accounts;
        data.clear();
        for (Account acc : accounts) {
            data.add(new AccountRow(acc.getId(),acc.getName(),acc.getAmount(),0));
        }
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

        positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
        accountNameInput = (TextInputLayout) dialog.getCustomView().findViewById(R.id.text_input_layout_add_account_name);
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
}
