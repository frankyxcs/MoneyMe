package com.devmoroz.moneyme.fragments;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.devmoroz.moneyme.MoneyApplication;
import com.devmoroz.moneyme.R;
import com.devmoroz.moneyme.models.Outcome;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChartFragment extends Fragment {

    private View view;
    private PieChart chart;
    private Typeface tf;
    private List<Outcome> outs;

    public static ChartFragment getInstance() {
        Bundle args = new Bundle();
        ChartFragment fragment = new ChartFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.chart_fragment, container, false);
        tf = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Regular.ttf");
        chart = (PieChart) view.findViewById(R.id.walletPieChart);

        outs = MoneyApplication.getInstance().outcomes;
        return view;
    }

    protected PieData generatePieData() {
        double totalAmount = 0;
        ArrayList<Entry> entries = new ArrayList<Entry>();
        String[] categories = getResources().getStringArray(R.array.transaction_category);
        ArrayList<String> xVals = new ArrayList<String>();
        xVals.addAll(Arrays.asList(categories));

        for (int i = 0; i < outs.size(); i++) {
            totalAmount+=outs.get(i).getAmount();
            entries.add(new Entry((float)outs.get(i).getAmount(),i));
        }

        PieDataSet ds1 = new PieDataSet(entries, String.valueOf(totalAmount));
        ds1.setColors(ColorTemplate.VORDIPLOM_COLORS);
        ds1.setSliceSpace(2f);
        ds1.setValueTextColor(Color.WHITE);
        ds1.setValueTextSize(12f);

        PieData d = new PieData(xVals, ds1);
        d.setValueTypeface(tf);

        return d;
    }
}
