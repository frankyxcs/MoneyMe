package com.devmoroz.moneyme.widgets;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.devmoroz.moneyme.R;
import com.devmoroz.moneyme.adapters.CategoryHistoryAdapter;
import com.devmoroz.moneyme.models.Currency;
import com.devmoroz.moneyme.models.LegendDetails;
import com.devmoroz.moneyme.models.Transaction;
import com.devmoroz.moneyme.utils.CurrencyCache;
import com.devmoroz.moneyme.utils.FormatUtils;

import java.util.Collections;
import java.util.List;

public class HistoryForCategoryDialog extends BottomSheetDialogFragment {

    private List<Transaction> transactionList = Collections.emptyList();
    private LegendDetails categoryDetails;

    private RecyclerView recyclerView;
    private TextView categoryTitle;
    private TextView categoryPercent;
    private TextView categoryAmount;
    private ImageView categoryColorCircle;

    private CategoryHistoryAdapter mAdapter;

    public static HistoryForCategoryDialog newInstance() {
        HistoryForCategoryDialog frag = new HistoryForCategoryDialog();
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.history_for_category_fragment, container, false);


        categoryTitle = (TextView) view.findViewById(R.id.categoryHistoryTitle);
        categoryPercent = (TextView) view.findViewById(R.id.categoryHistoryPercentage);
        categoryAmount = (TextView) view.findViewById(R.id.categoryHistoryAmount);
        categoryColorCircle = (ImageView) view.findViewById(R.id.categoryHistoryCircle);
        recyclerView = (RecyclerView) view.findViewById(R.id.categoryHistoryRecycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAdapter = new CategoryHistoryAdapter(getActivity(), new CategoryHistoryAdapter.Callback() {
            @Override
            public void onRowClick(Transaction transaction) {

            }

            @Override
            public void onRowLongClick(Transaction transaction) {

            }
        });

        mAdapter.setTransactions(transactionList);
        recyclerView.setAdapter(mAdapter);

        setCategoryDetails();

        return view;
    }

    public void setData(LegendDetails details,List<Transaction> transactionList){
        this.categoryDetails = details;
        this.transactionList = transactionList;
    }

    private void setCategoryDetails(){
        Currency currency = CurrencyCache.getCurrencyOrEmpty();
        categoryTitle.setText(categoryDetails.CategoryName);
        categoryPercent.setText(FormatUtils.roundValueToStringWithPercent(categoryDetails.AmountPercent));
        categoryColorCircle.setColorFilter(categoryDetails.ColorCode);
        categoryAmount.setText(FormatUtils.amountToString(currency, categoryDetails.Amount));
    }
}
