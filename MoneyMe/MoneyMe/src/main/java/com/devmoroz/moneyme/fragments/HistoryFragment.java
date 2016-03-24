package com.devmoroz.moneyme.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.devmoroz.moneyme.MoneyApplication;
import com.devmoroz.moneyme.R;
import com.devmoroz.moneyme.adapters.HistoryAdapter;
import com.devmoroz.moneyme.eventBus.BusProvider;
import com.devmoroz.moneyme.eventBus.SearchCanceled;
import com.devmoroz.moneyme.eventBus.SearchTriggered;
import com.devmoroz.moneyme.eventBus.WalletChangeEvent;
import com.devmoroz.moneyme.models.Account;
import com.devmoroz.moneyme.models.Currency;
import com.devmoroz.moneyme.models.Transaction;
import com.devmoroz.moneyme.models.TransactionType;
import com.devmoroz.moneyme.utils.CommonUtils;
import com.devmoroz.moneyme.utils.CurrencyCache;
import com.devmoroz.moneyme.utils.FormatUtils;
import com.devmoroz.moneyme.utils.Preferences;
import com.devmoroz.moneyme.utils.datetime.DataInterval;
import com.devmoroz.moneyme.utils.datetime.PeriodUtils;
import com.devmoroz.moneyme.utils.datetime.TimeUtils;
import com.devmoroz.moneyme.widgets.EmptyRecyclerView;
import com.devmoroz.moneyme.widgets.SectionedRecyclerViewAdapter;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.squareup.otto.Subscribe;


import org.joda.time.Interval;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.grantland.widget.AutofitTextView;

public class HistoryFragment extends Fragment {

    private final int ACTION_SEARCHED_TRIGGERED = 1;
    private final int ACTION_SEARCHED_CANCELED = 2;

    private List<Transaction> mListWalletEntries = Collections.emptyList();
    private List<SectionedRecyclerViewAdapter.Section> mSections;
    private List<Account> accounts;
    double totalBalance;

    private CommonUtils sorter = new CommonUtils();

    private EmptyRecyclerView recyclerView;
    private LinearLayout mTextError;
    private AutofitTextView totalBalanceTextView;
    private TextView walletTotalIncome;
    private TextView walletTotalOutcome;
    private AutofitTextView walletDatePeriod;
    private View view;
    private FloatingActionsMenu fab;
    private boolean isEmptyTextChanged = false;

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
        totalBalanceTextView = (AutofitTextView) view.findViewById(R.id.dashboard_total_balance);
        walletTotalIncome = (TextView) view.findViewById(R.id.walletTotalIncome);
        walletTotalOutcome = (TextView) view.findViewById(R.id.walletTotalOutcome);
        walletDatePeriod = (AutofitTextView) view.findViewById(R.id.text_date_period);
        LinearLayout widg = (LinearLayout) view.findViewById(R.id.widgetBalance);

        recyclerView = (EmptyRecyclerView) view.findViewById(R.id.main_recycler_view);
        recyclerView.setEmptyView(mTextError, widg);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        wAdapter = new HistoryAdapter(getActivity(), new HistoryAdapter.Callback() {
            @Override
            public void onDeleteClick(String id, TransactionType type) {
                final String itemId = id;
                final TransactionType itemType = type;
                new MaterialDialog.Builder(getContext())
                        .content(R.string.remove_item_confirm)
                        .negativeText(R.string.cancel)
                        .positiveText(R.string.remove)
                        .positiveColorRes(R.color.colorPrimary)
                        .negativeColorRes(R.color.colorPrimary)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                                if (CommonUtils.deleteItem(itemId, itemType) == 1) {
                                    BusProvider.postOnMain(new WalletChangeEvent());
                                }
                            }
                        })
                        .show();
            }

            @Override
            public void onEditClick(int position) {
                Transaction transaction = wAdapter.getTransationByPosition(position);
            }
        });
        boolean desc = Preferences.isSortByDesc(getContext());

        mListWalletEntries = MoneyApplication.getInstance().getTransactions();
        sorter.sortWalletEntriesByDate(mListWalletEntries, desc);
        wAdapter.setInOutData(mListWalletEntries);

        initBalanceWidget(desc);
        initHistoryAdapter(mListWalletEntries);
        return view;
    }

    private void initHistoryAdapter(List<Transaction> list) {
        mSections = new ArrayList<>();
        long previousTime = -1;
        long time;
        int position = 0;
        for (Transaction item : list) {
            time = item.getDateLong();
            if (!TimeUtils.isSameDayDisplay(previousTime, time)) {
                mSections.add(new SectionedRecyclerViewAdapter.Section(position,
                        TimeUtils.formatHumanFriendlyShortDate(getContext(), time)
                ));
            }
            ++position;
            previousTime = time;
        }
        SectionedRecyclerViewAdapter sectionAdapter = new SectionedRecyclerViewAdapter(getContext(), R.layout.section, R.id.section_text, wAdapter);
        SectionedRecyclerViewAdapter.Section[] dummy = new SectionedRecyclerViewAdapter.Section[mSections.size()];
        sectionAdapter.setSections(mSections.toArray(dummy));

        recyclerView.setAdapter(sectionAdapter);
    }

    private void initBalanceWidget(boolean order) {
        if (!mListWalletEntries.isEmpty()) {
            int period = Preferences.getHistoryPeriod(getContext());
            int monthStart = Preferences.getMonthStart(getContext());
            accounts = MoneyApplication.getInstance().getAccounts();
            totalBalance = 0f;
            for (Account acc : accounts) {
                totalBalance += acc.getBalance();
            }
            Currency currency = CurrencyCache.getCurrencyOrEmpty();
            String text = getString(R.string.balance);
            String formattedBalanceText = FormatUtils.attachAmountToTextWithoutBrackets(text, currency, totalBalance, false);
            String[] totalInOut = sorter.getTotalInOut(mListWalletEntries, currency);
            walletTotalIncome.setText(totalInOut[0]);
            walletTotalOutcome.setText(totalInOut[1]);
            totalBalanceTextView.setText(formattedBalanceText);
            if (period != 0) {
                final Interval historyInterval = DataInterval.getHistoryInterval(System.currentTimeMillis(), period, monthStart);
                long start;
                long end = historyInterval.getEndMillis();
                if(monthStart == 1){
                    start = historyInterval.getStartMillis();
                }else{
                    start = historyInterval.getStart().minusDays(1).getMillis();
                }
                walletDatePeriod.setText(TimeUtils.formatIntervalTimeString(start,end, null, getContext()));
            } else {
                long dateStart;
                if (order) {
                    dateStart = mListWalletEntries.get(mListWalletEntries.size() - 1).getDateLong();
                } else {
                    dateStart = mListWalletEntries.get(0).getDateLong();
                }
                walletDatePeriod.setText(TimeUtils.formatIntervalTimeString(dateStart, null, getContext()));
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public void CheckWallet() {
        mListWalletEntries = MoneyApplication.getInstance().getTransactions();
        boolean desc = Preferences.isSortByDesc(getContext());
        sorter.sortWalletEntriesByDate(mListWalletEntries, desc);
        initBalanceWidget(desc);
        wAdapter.setInOutData(mListWalletEntries);
        initHistoryAdapter(mListWalletEntries);
    }

    @Subscribe
    public void OnWalletChange(WalletChangeEvent event) {
        CheckWallet();
    }
    @Subscribe
    public void OnSearchTriggered(SearchTriggered event) {
        String term = event.term.toLowerCase();
        ArrayList<Transaction> searchedItems = new ArrayList<>();
        mListWalletEntries = MoneyApplication.getInstance().getTransactions();
        for (Transaction item : mListWalletEntries) {
            String notes = item.getNotes() != null ? item.getNotes() : "";
            String category = item.getCategory() != null ? item.getCategory().getTitle() : "";
            String accountFromName = item.getAccountFrom() != null ? item.getAccountFromName(): "";
            String accountToName = item.getAccountTo() != null ? item.getAccountToName() : "";
            String tags = item.getTags() != null ? item.getTags() : "";
            String locationName = item.getLocationName() != null ? item.getLocationName() : "";
            if (category.toLowerCase().contains(term) || notes.toLowerCase().contains(term) || accountFromName.toLowerCase().contains(term) || accountToName.toLowerCase().contains(term) || tags.toLowerCase().contains(term) || locationName.toLowerCase().contains(term)) {
                searchedItems.add(item);
            }
        }
        changeEmptyText(ACTION_SEARCHED_TRIGGERED);
        wAdapter.setInOutData(searchedItems);
        initHistoryAdapter(searchedItems);
    }

    private void changeEmptyText(int action) {
        TextView text1 = (TextView) mTextError.findViewById(R.id.walletHistoryEmptyText1);
        TextView text2 = (TextView) mTextError.findViewById(R.id.walletHistoryEmptyText2);
        switch (action){
            case ACTION_SEARCHED_TRIGGERED:
                text1.setText(R.string.no_transactions_found);
                text2.setVisibility(View.INVISIBLE);
                break;
            case ACTION_SEARCHED_CANCELED:
                text1.setText(R.string.no_data_available);
                text2.setVisibility(View.VISIBLE);
                break;
        }

    }

    @Subscribe
    public void OnSearchCanceled(SearchCanceled event) {
        changeEmptyText(ACTION_SEARCHED_CANCELED);
        CheckWallet();
    }
}
