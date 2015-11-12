package com.devmoroz.moneyme.fragments;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.devmoroz.moneyme.eventBus.BusProvider;
import com.devmoroz.moneyme.eventBus.WalletChangeEvent;
import com.devmoroz.moneyme.models.Income;
import com.devmoroz.moneyme.models.Outcome;
import com.devmoroz.moneyme.utils.CurrencyCache;
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
    private LinearLayout childlayout;

    private List<Outcome> outs;
    private List<Income> ins;
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
        childlayout = (LinearLayout) view.findViewById(R.id.childLayout);

        chart.setHoleRadius(50f);
        chart.setTransparentCircleRadius(53f);
        chart.setExtraOffsets(5, 10, 5, 5);
        chart.setDragDecelerationFrictionCoef(0.95f);



        outs = MoneyApplication.getInstance().outcomes;
        ins = MoneyApplication.getInstance().incomes;

        chart.setData(generatePieData());
        chart.setCenterText(generateCenterText());
        chart.setCenterTextSize(16f);
        chart.setDescription("");
        chart.setOnChartValueSelectedListener(this);
        chart.invalidate();

        customizeLegend();

        return view;
    }

    private void customizeLegend() {
        Legend l = chart.getLegend();
        //l.setEnabled(false);
        l.setPosition(Legend.LegendPosition.ABOVE_CHART_LEFT);
        l.setWordWrapEnabled(true);
        /*l.setEnabled(false);
        int colorcodes[] = l.getColors();

        for (int i = 0; i < l.getColors().length - 1; i++) {
            LinearLayout.LayoutParams parms_left_layout = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            parms_left_layout.weight = 1F;
            LinearLayout left_layout = new LinearLayout(getContext());
            left_layout.setOrientation(LinearLayout.HORIZONTAL);
            left_layout.setLayoutParams(parms_left_layout);

            LinearLayout.LayoutParams parms_legen_layout = new LinearLayout.LayoutParams(
                    20, 20);
            parms_legen_layout.setMargins(0, 0, 20, 0);
            LinearLayout legend_layout = new LinearLayout(getContext());
            legend_layout.setLayoutParams(parms_legen_layout);
            legend_layout.setOrientation(LinearLayout.HORIZONTAL);
            legend_layout.setBackgroundColor(colorcodes[i]);
            left_layout.addView(legend_layout);

            TextView txt_unit = new TextView(getContext());
            txt_unit.setText(l.getLabel(i));
            left_layout.addView(txt_unit);

            LinearLayout.LayoutParams parms_middle_layout = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            parms_middle_layout.weight = 1F;
            LinearLayout middle_layout = new LinearLayout(getContext());
            middle_layout.setOrientation(LinearLayout.HORIZONTAL);
            middle_layout.setLayoutParams(parms_middle_layout);

            TextView txt_leads = new TextView(getContext());
            txt_leads.setText("450");
            middle_layout.addView(txt_leads);

            LinearLayout.LayoutParams parms_right_layout = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            parms_right_layout.weight = 1F;
            LinearLayout right_layout = new LinearLayout(getContext());
            right_layout.setOrientation(LinearLayout.HORIZONTAL);
            right_layout.setLayoutParams(parms_right_layout);

            TextView txt_leads_percentage = new TextView(getContext());
            txt_leads_percentage.setText(40 + "");
            right_layout.addView(txt_leads_percentage);

            LinearLayout.LayoutParams main_layout_params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            LinearLayout main_layout = new LinearLayout(getContext());
            main_layout.setOrientation(LinearLayout.HORIZONTAL);
            main_layout.setLayoutParams(main_layout_params);

            main_layout.addView(left_layout);
            main_layout.addView(middle_layout);
            main_layout.addView(right_layout);

            childlayout.addView(main_layout);
        }*/

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

        for (Income in : ins) {
            totalBalance += in.getAmount();
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

        totalBalance -= totalAmount;
        String sign = CurrencyCache.getCurrencyOrEmpty().getSymbol();
        balance = String.format("%10.2f %s", totalBalance, sign);
        totalOut = String.format("%10.2f %s", totalAmount, sign);
        PieDataSet ds1 = new PieDataSet(entries, "");
        ds1.setColors(ColorTemplate.VORDIPLOM_COLORS);
        ds1.setSliceSpace(2f);
        ds1.setValueTextColor(Color.WHITE);
        ds1.setValueTextSize(10f);

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
        ins = MoneyApplication.getInstance().incomes;
        chart.setData(generatePieData());
        chart.setCenterText(generateCenterText());
        chart.invalidate();
        chart.animateY(1400);
    }
}
