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

import com.devmoroz.moneyme.R;
import com.devmoroz.moneyme.adapters.AccountsAdapter;
import com.devmoroz.moneyme.eventBus.BusProvider;
import com.devmoroz.moneyme.eventBus.WalletChangeEvent;
import com.devmoroz.moneyme.models.AccountRow;
import com.devmoroz.moneyme.utils.DividerItemDecoration;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

public class AccountsFragment extends Fragment {

    private View view;
    private RecyclerView recyclerView;
    private AccountsAdapter adapter;
    private ArrayList<AccountRow> data = new ArrayList<>();

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
        data.add(new AccountRow(1,"Bank",5200f,300f));
        data.add(new AccountRow(2,"Credit card",1500f,3200f));
        view = inflater.inflate(R.layout.accounts_fragment, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.accountsList);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL_LIST));

        adapter = new AccountsAdapter(getActivity(),data);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }

    @Subscribe
    public void OnWalletChange(WalletChangeEvent event){

    }
}
