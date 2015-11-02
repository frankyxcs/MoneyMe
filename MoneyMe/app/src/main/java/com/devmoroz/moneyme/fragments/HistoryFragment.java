package com.devmoroz.moneyme.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.devmoroz.moneyme.AddItemActivity;
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
    private ArrayList<CommonInOut> inout;
    public static int currentItem;

    private CommonInOutSorter sorter = new CommonInOutSorter();

    private RecyclerView wRecycleWalletEntries;
    private TextView mTextError;
    private View view;
    static View.OnClickListener cardOnClickListener;

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
        cardOnClickListener = new CardOnClickListener(getContext());

        if (savedInstanceState != null) {
            mListWalletEntries = savedInstanceState.getParcelableArrayList(STATE_WALLET_ENTRIES);
        } else {
            inout = MoneyApplication.inout;
            if (inout != null && !inout.isEmpty()) {
                mListWalletEntries = inout;
                sorter.sortWalletEntriesByDate(mListWalletEntries);
                wAdapter.setInOutData(mListWalletEntries);
            } else {
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

    private class CardOnClickListener implements View.OnClickListener {
        private final Context context;

        private CardOnClickListener(Context c) {
            this.context = c;
        }

        @Override
        public void onClick(View v) {
            currentItem = wRecycleWalletEntries.getChildAdapterPosition(v);
            startActivity(new Intent(getContext(), AddItemActivity.class));
        }
    }
}
