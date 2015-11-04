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
import com.devmoroz.moneyme.models.ChartVM;
import com.devmoroz.moneyme.models.Outcome;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

        chart.setHoleRadius(45f);
        chart.setTransparentCircleRadius(50f);

        Legend l = chart.getLegend();
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);

        outs = MoneyApplication.getInstance().outcomes;

        chart.setData(generatePieData());
        chart.invalidate();
        return view;
    }

    protected PieData generatePieData() {
        float totalAmount = 0;
        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<String> xVals = new ArrayList<>();
        HashMap<String,Float> data = new HashMap<>();


        for (int i = 0; i < outs.size(); i++) {
            Outcome elem = outs.get(i);
            String key = elem.getCategory();
            float val = (float)elem.getAmount();

            totalAmount += val;

            if(data.containsKey(key)){
                data.put(key, data.get(key)+val);
            }else{
                data.put(key,val);
            }
        }

        if(!data.isEmpty()){
            int j = 0;
            Set<Map.Entry<String, Float>> set = data.entrySet();
            for (Map.Entry<String, Float> entry : set) {
                xVals.add(entry.getKey());
                entries.add(new Entry(entry.getValue(),j));
                j++;
            }
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
