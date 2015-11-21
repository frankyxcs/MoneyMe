package com.devmoroz.moneyme.fragments;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.devmoroz.moneyme.MoneyApplication;
import com.devmoroz.moneyme.R;
import com.devmoroz.moneyme.adapters.ChartLegendAdapter;
import com.devmoroz.moneyme.eventBus.BusProvider;
import com.devmoroz.moneyme.eventBus.WalletChangeEvent;
import com.devmoroz.moneyme.models.Account;
import com.devmoroz.moneyme.models.Income;
import com.devmoroz.moneyme.models.LegendDetails;
import com.devmoroz.moneyme.models.Outcome;
import com.devmoroz.moneyme.utils.CurrencyCache;
import com.devmoroz.moneyme.utils.CustomColorTemplate;
import com.devmoroz.moneyme.widgets.DividerItemDecoration;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ChartFragment extends Fragment implements OnChartValueSelectedListener {

    private View view;
    private PieChart chart;
    private RecyclerView recyclerView;
    ChartLegendAdapter adapter;
    LinearLayout tableDetails;

    private List<Outcome> outs;
    private List<Account> accounts;
    private String totalOut;
    private String balance;


    public static ChartFragment getInstance() {
        Bundle args = new Bundle();
        ChartFragment fragment = new ChartFragment();
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
        view = inflater.inflate(R.layout.chart_fragment, container, false);
        chart = (PieChart) view.findViewById(R.id.walletPieChart);
        tableDetails = (LinearLayout) view.findViewById(R.id.chartDetailsTable);
        recyclerView = (RecyclerView) view.findViewById(R.id.chartLegendsRecycler);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));


        adapter = new ChartLegendAdapter(getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        chart.setHoleRadius(50f);
        chart.setTransparentCircleRadius(53f);
        chart.setExtraOffsets(5, 10, 5, 5);
        chart.setDragDecelerationFrictionCoef(0.95f);



        outs = MoneyApplication.getInstance().outcomes;
        accounts = MoneyApplication.getInstance().accounts;

        chart.setData(generatePieData());
        chart.setCenterText(generateCenterText());
        chart.setCenterTextSize(16f);
        chart.setDescription("Данные за текущий месяц");
        chart.setOnChartValueSelectedListener(this);
        chart.invalidate();

        customizeLegend();

        return view;
    }

    private void customizeLegend() {
        ArrayList<LegendDetails> legendData = new ArrayList<>();
        Legend l = chart.getLegend();
        l.setEnabled(false);
        int colorCodes[] = l.getColors();
        PieData data = chart.getData();
        List<Entry> entries = data.getDataSet().getYVals();

        for (int i = 0; i < l.getColors().length - 1; i++) {
            Entry entry = entries.get(i);
            String label = l.getLabel(i);
            int colorCode = colorCodes[i];
            float val = entry.getVal();
            float valPercent = entry.getVal() / data.getYValueSum() * 100f;
            legendData.add(new LegendDetails(colorCode,label,val,valPercent));
        }

        if (legendData.size() != 0){
            adapter.setData(legendData);
            tableDetails.setVisibility(View.VISIBLE);
        }else{
            tableDetails.setVisibility(View.GONE);
        }
    }


    private SpannableString generateCenterText() {
        String centerText = String.format("%s\n%s", totalOut, balance);
        SpannableString s = new SpannableString(centerText);
        s.setSpan(new ForegroundColorSpan(Color.RED), 0, totalOut.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        s.setSpan(new ForegroundColorSpan(Color.GREEN), totalOut.length(), s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return s;
    }

    protected PieData generatePieData() {
        float totalAmount = 0;
        float totalBalance = 0;
        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<String> xVals = new ArrayList<>();
        HashMap<String, Float> data = new HashMap<>();

        for (Account acc : accounts) {
            totalBalance += acc.getBalance();
        }

        for (int i = 0; i < outs.size(); i++) {
            Outcome elem = outs.get(i);
            String key = elem.getCategory();
            float val = (float) elem.getAmount();

            totalAmount += val;

            if (data.containsKey(key)) {
                data.put(key, data.get(key) + val);
            } else {
                data.put(key, val);
            }
        }

        if (!data.isEmpty()) {
            int j = 0;
            Set<Map.Entry<String, Float>> set = data.entrySet();
            for (Map.Entry<String, Float> entry : set) {
                xVals.add(entry.getKey());
                entries.add(new Entry(entry.getValue(), j));
                j++;
            }
        }

        String sign = CurrencyCache.getCurrencyOrEmpty().getSymbol();
        balance = String.format("%10.2f %s", totalBalance, sign).trim();
        totalOut = String.format("%10.2f %s", totalAmount, sign).trim();
        PieDataSet ds1 = new PieDataSet(entries, "");
        ds1.setColors(CustomColorTemplate.PIECHART_COLORS);
        ds1.setSliceSpace(2f);
        ds1.setValueTextColor(CustomColorTemplate.SECONDARY_TEXT);
        ds1.setValueTextSize(8f);
        ds1.setDrawValues(false);


        PieData d = new PieData(xVals, ds1);

        return d;
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        float rotationAngle = chart.getRotationAngle();
        float[] mAbsoluteAngles = chart.getAbsoluteAngles();
        float[] mDrawAngles = chart.getDrawAngles();
        int i = e.getXIndex();

        float offset = mDrawAngles[i] / 2;

        // calculate the next position
        float end = 270f - (mAbsoluteAngles[i] - offset);

        //rotate to slice center
        chart.spin(1000, rotationAngle, end, Easing.EasingOption.EaseInOutQuad);
    }

    @Override
    public void onNothingSelected() {

    }

    @Subscribe
    public void OnWalletChange(WalletChangeEvent event) {
        outs = MoneyApplication.getInstance().outcomes;
        accounts = MoneyApplication.getInstance().accounts;
        chart.setData(generatePieData());
        chart.setCenterText(generateCenterText());
        chart.invalidate();
        chart.animateY(1400);
        customizeLegend();
    }
}
