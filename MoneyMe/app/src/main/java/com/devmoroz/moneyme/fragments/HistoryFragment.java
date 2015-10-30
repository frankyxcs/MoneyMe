package com.devmoroz.moneyme.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.devmoroz.moneyme.MoneyApplication;
import com.devmoroz.moneyme.R;
import com.devmoroz.moneyme.adapters.HistoryAdapter;
import com.devmoroz.moneyme.eventBus.BusProvider;
import com.devmoroz.moneyme.models.CommonInOut;
import com.devmoroz.moneyme.models.Income;
import com.devmoroz.moneyme.models.Outcome;
import com.devmoroz.moneyme.utils.CommonInOutSorter;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {

    private static final String STATE_WALLET_ENTRIES = "state_wallet_entries";
    private ArrayList<CommonInOut> mListWalletEntries = new ArrayList<>();
    private List<Income> incomes;
    private List<Outcome> outcomes;
    
    private CommonInOutSorter sorter = new CommonInOutSorter();

    private RecyclerView wRecycleWalletEntries;
    private TextView mTextError;
    private View view;

    private HistoryAdapter wAdapter;

    public static HistoryFragment getInstance() {
        Bundle args = new Bundle();
        HistoryFragment fragment = new HistoryFragment();
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
        view = inflater.inflate(R.layout.history_fragment, container, false);
        mTextError = (TextView) view.findViewById(R.id.textWalletHistoryError);
        wRecycleWalletEntries = (RecyclerView) view.findViewById(R.id.main_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        wRecycleWalletEntries.setLayoutManager(layoutManager);
        wAdapter = new HistoryAdapter(getActivity());
        wRecycleWalletEntries.setAdapter(wAdapter);

        if(savedInstanceState!=null){
            mListWalletEntries = savedInstanceState.getParcelableArrayList(STATE_WALLET_ENTRIES);
        } else{
            incomes = MoneyApplication.incomes;
            outcomes = MoneyApplication.outcomes;

            if(incomes!=null){
            for (Income in:incomes) {
                CommonInOut inout = new CommonInOut(1,in.getId(),in.getAmount(),in.getNotes(),in.getDateOfReceipt());
                mListWalletEntries.add(inout);
            }} if(outcomes!=null){
            for (Outcome out:outcomes) {
                CommonInOut inout = new CommonInOut(2,out.getId(),out.getAmount(),out.getNotes(),out.getDateOfSpending());
                mListWalletEntries.add(inout);
            }}

            if(!mListWalletEntries.isEmpty()){
                sorter.sortWalletEntriesByDate(mListWalletEntries);
                wAdapter.setInOutData(mListWalletEntries);
            }else{
                mTextError.setVisibility(View.VISIBLE);
            }

        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //save the wallet entries list to a parcelable prior to rotation or configuration change
        outState.putParcelableArrayList(STATE_WALLET_ENTRIES, mListWalletEntries);
    }
}
