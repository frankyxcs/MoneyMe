package com.devmoroz.moneyme.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.devmoroz.moneyme.MoneyApplication;
import com.devmoroz.moneyme.R;
import com.devmoroz.moneyme.adapters.AccountsAdapter;
import com.devmoroz.moneyme.eventBus.BusProvider;
import com.devmoroz.moneyme.eventBus.WalletChangeEvent;
import com.devmoroz.moneyme.models.Account;
import com.devmoroz.moneyme.models.AccountRow;
import com.devmoroz.moneyme.utils.DividerItemDecoration;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class AccountsFragment extends Fragment {

    private View view;
    private RecyclerView recyclerView;
    private AccountsAdapter adapter;
    private ArrayList<AccountRow> data = new ArrayList<>();
    private List<Account> accounts;

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
        recyclerView = (RecyclerView) view.findViewById(R.id.accountsList);
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

    @Subscribe
    public void OnWalletChange(WalletChangeEvent event) {
       CheckWallet();
    }
}
