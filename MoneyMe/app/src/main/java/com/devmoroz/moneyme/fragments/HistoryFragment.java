package com.devmoroz.moneyme.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.devmoroz.moneyme.MoneyApplication;
import com.devmoroz.moneyme.R;
import com.devmoroz.moneyme.adapters.HistoryAdapter;
import com.devmoroz.moneyme.eventBus.BusProvider;
import com.devmoroz.moneyme.eventBus.WalletChangeEvent;
import com.devmoroz.moneyme.models.Account;
import com.devmoroz.moneyme.models.CommonInOut;
import com.devmoroz.moneyme.models.Currency;
import com.devmoroz.moneyme.utils.CommonInOutUtils;
import com.devmoroz.moneyme.utils.CurrencyCache;
import com.devmoroz.moneyme.utils.FormatUtils;
import com.devmoroz.moneyme.utils.Preferences;
import com.devmoroz.moneyme.utils.datetime.PeriodUtils;
import com.devmoroz.moneyme.widgets.CustomRecyclerScroll;
import com.devmoroz.moneyme.widgets.EmptyRecyclerView;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.squareup.otto.Subscribe;


import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {

    private static final String STATE_WALLET_ENTRIES = "state_wallet_entries";
    private ArrayList<CommonInOut> mListWalletEntries = new ArrayList<>();
    private ArrayList<CommonInOut> inout;
    private List<Account> accounts;
    int totalBalance;

    private CommonInOutUtils sorter = new CommonInOutUtils();

    private EmptyRecyclerView recyclerView;
    private LinearLayout mTextError;
    private TextView totalBalanceTextView;
    private TextView walletTotalIncome;
    private TextView walletTotalOutcome;
    private TextView walletDatePeriod;
    private View view;
    private FloatingActionsMenu fab;

    private HistoryAdapter wAdapter;

    public void setFab(FloatingActionsMenu fab) {
        this.fab = fab;
    }

    public static HistoryFragment getInstance(FloatingActionsMenu fab) {
        Bundle args = new Bundle();
        HistoryFragment fragment = new HistoryFragment();
        fragment.setFab(fab);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
        CheckWallet();
    }

    @Override
    public void onPause() {
        BusProvider.getInstance().unregister(this);
        super.onPause();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.history_fragment, container, false);
        mTextError = (LinearLayout) view.findViewById(R.id.walletHistoryEmpty);
        totalBalanceTextView = (TextView) view.findViewById(R.id.dashboard_total_balance);
        walletTotalIncome = (TextView) view.findViewById(R.id.walletTotalIncome);
        walletTotalOutcome = (TextView) view.findViewById(R.id.walletTotalOutcome);
        walletDatePeriod = (TextView) view.findViewById(R.id.text_date_period);
        LinearLayout widg = (LinearLayout) view.findViewById(R.id.widgetBalance);

        recyclerView = (EmptyRecyclerView) view.findViewById(R.id.main_recycler_view);
        recyclerView.setEmptyView(mTextError,widg);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        wAdapter = new HistoryAdapter(getActivity());
        recyclerView.setAdapter(wAdapter);

        recyclerView.addOnScrollListener(new CustomRecyclerScroll() {
            @Override
            public void show() {
                if (fab != null) {
                    fab.collapse();
                    fab.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
                }
            }

            @Override
            public void hide() {
                if (fab != null) {
                    fab.collapse();
                    fab.animate().translationY(fab.getHeight() + TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics())).setInterpolator(new AccelerateInterpolator(2)).start();
                }
            }
        });


        if (savedInstanceState != null) {
            mListWalletEntries = savedInstanceState.getParcelableArrayList(STATE_WALLET_ENTRIES);
        } else {
            inout = MoneyApplication.inout;
            if (inout != null && !inout.isEmpty()) {
                mListWalletEntries = inout;
                boolean desc = Preferences.isSortByDesc(getContext());
                sorter.sortWalletEntriesByDate(mListWalletEntries,desc);
                wAdapter.setInOutData(mListWalletEntries);
            } else {
                mTextError.setVisibility(View.VISIBLE);
            }
        }
        initBalanceWidget();
        return view;
    }

    private void initBalanceWidget() {
        int period = Preferences.getHistoryPeriod(getContext());
        int monthStart = Preferences.getMonthStart(getContext());
        accounts = MoneyApplication.accounts;
        totalBalance = 0;
        for (Account acc : accounts) {
            totalBalance += acc.getBalance();
        }
        Currency currency = CurrencyCache.getCurrencyOrEmpty();
        String text = getString(R.string.balance);
        String formattedBalanceText = FormatUtils.attachAmountToTextWithoutBrackets(text,currency,totalBalance,false);
        String[] totalInOut = sorter.getTotalInOut(mListWalletEntries,currency);
        walletTotalIncome.setText(totalInOut[0]);
        walletTotalOutcome.setText(totalInOut[1]);
        totalBalanceTextView.setText(formattedBalanceText);
        walletDatePeriod.setText(PeriodUtils.GetPeriodString(period,monthStart,getContext(),false));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //save the wallet entries list to a parcelable prior to rotation or configuration change
        outState.putParcelableArrayList(STATE_WALLET_ENTRIES, mListWalletEntries);
    }

    public void CheckWallet() {
        mListWalletEntries = MoneyApplication.inout;
        initBalanceWidget();
        boolean desc = Preferences.isSortByDesc(getContext());
        sorter.sortWalletEntriesByDate(mListWalletEntries,desc);
        wAdapter.setInOutData(mListWalletEntries);
    }

    @Subscribe
    public void OnWalletChange(WalletChangeEvent event){
        CheckWallet();
    }
}
