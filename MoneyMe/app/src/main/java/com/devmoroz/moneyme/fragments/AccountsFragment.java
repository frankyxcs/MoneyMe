package com.devmoroz.moneyme.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.afollestad.materialdialogs.MaterialDialog;
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
        boolean wrapInScrollView = true;
        MaterialDialog dialog = new MaterialDialog.Builder(getContext())
                .title(R.string.add_account)
                .inputRangeRes(4, 20, R.color.holo_red_dark)
                .inputType(InputType.TYPE_CLASS_TEXT)
                .negativeText(R.string.cancel)
                .positiveText(R.string.save)
                .positiveColorRes(R.color.colorPrimary)
                .negativeColorRes(R.color.colorPrimary)
                .widgetColorRes(R.color.colorPrimary)
                .dividerColorRes(R.color.colorPrimaryDark)
                .input(R.string.account_name, R.string.account_name, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        // Do something
                    }
                })
                .show();

    }

    @Subscribe
    public void OnWalletChange(WalletChangeEvent event) {
       CheckWallet();
    }
}
