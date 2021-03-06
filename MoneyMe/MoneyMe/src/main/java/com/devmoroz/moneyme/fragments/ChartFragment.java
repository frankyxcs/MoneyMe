package com.devmoroz.moneyme.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.devmoroz.moneyme.MoneyApplication;
import com.devmoroz.moneyme.R;
import com.devmoroz.moneyme.adapters.ChartLegendAdapter;
import com.devmoroz.moneyme.eventBus.BusProvider;
import com.devmoroz.moneyme.eventBus.ChartSliceClickedEvent;
import com.devmoroz.moneyme.eventBus.WalletChangeEvent;
import com.devmoroz.moneyme.helpers.DBHelper;
import com.devmoroz.moneyme.models.Account;
import com.devmoroz.moneyme.models.LegendDetails;
import com.devmoroz.moneyme.models.Transaction;
import com.devmoroz.moneyme.models.TransactionType;
import com.devmoroz.moneyme.utils.CommonUtils;
import com.devmoroz.moneyme.utils.CurrencyCache;
import com.devmoroz.moneyme.utils.CustomColorTemplate;
import com.devmoroz.moneyme.utils.Preferences;
import com.devmoroz.moneyme.utils.datetime.DataInterval;
import com.devmoroz.moneyme.utils.datetime.PeriodUtils;
import com.devmoroz.moneyme.utils.datetime.TimeUtils;
import com.devmoroz.moneyme.widgets.DividerItemDecoration;
import com.devmoroz.moneyme.widgets.HistoryForCategoryDialog;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.squareup.otto.Subscribe;

import org.joda.time.Interval;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ChartFragment extends Fragment implements OnChartValueSelectedListener {

    public static final int NO_DATA_COLOR = Color.LTGRAY;

    private View view;
    private PieChart chart;
    private RecyclerView recyclerView;
    ChartLegendAdapter adapter;
    LinearLayout tableDetails;

    private List<Transaction> outs;
    private List<Account> accounts;
    private String totalOut;
    private String balance;

    ArrayList<LegendDetails> legendData = new ArrayList<>();


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
        BusProvider.getInstance().unregister(this);
        super.onPause();
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

        chart.setOnChartValueSelectedListener(this);

        getDataForCurrentMonth();
        accounts = MoneyApplication.getInstance().getAccounts();

        displayChart();

        return view;
    }

    private void getDataForCurrentMonth() {
        try {
            int monthStart = Preferences.getMonthStart(getContext());
            DBHelper helper = MoneyApplication.GetDBHelper();
            outs = helper.getTransactionDAO().queryByTypeForCurrentMonth(monthStart - 1, TransactionType.OUTCOME);
        } catch (SQLException ex) {

        }
    }

    private void displayChart() {
        chart.highlightValues(null);
        chart.clear();

        PieData pieData = generatePieData();
        if (pieData != null && pieData.getYValCount() != 0) {
            chart.setData(pieData);
            chart.setCenterText(generateCenterText());
            chart.setCenterTextSize(16f);
            chart.setTouchEnabled(true);
            chart.invalidate();
            chart.animateXY(1400, 1400);
            customizeLegend();
        } else {
            chart.setCenterText(getResources().getString(R.string.chart_no_data));
            chart.setCenterTextSize(12f);
            chart.setCenterTextColor(CustomColorTemplate.ACCENT_COLOR);
            chart.setData(getEmptyData());
            chart.setTouchEnabled(false);
            chart.invalidate();
            chart.setDescription("");
            chart.getLegend().setEnabled(false);
            tableDetails.setVisibility(View.GONE);
        }
    }

    private void bubbleSort() {
        List<String> labels = chart.getData().getXVals();
        List<Entry> values = ((PieDataSet) chart.getData().getDataSet()).getYVals();
        List<Integer> colors = chart.getData().getDataSet().getColors();
        float tmp1;
        String tmp2;
        Integer tmp3;
        for(int i = 0; i < values.size() - 1; i++) {
            for(int j = 1; j < values.size() - i; j++) {
                if (values.get(j-1).getVal() > values.get(j).getVal()) {
                    tmp1 = values.get(j - 1).getVal();
                    values.get(j - 1).setVal(values.get(j).getVal());
                    values.get(j).setVal(tmp1);

                    tmp2 = labels.get(j - 1);
                    labels.set(j - 1, labels.get(j));
                    labels.set(j, tmp2);

                    tmp3 = colors.get(j - 1);
                    colors.set(j - 1, colors.get(j));
                    colors.set(j, tmp3);
                }
            }
        }

        chart.notifyDataSetChanged();
        chart.highlightValues(null);
        chart.invalidate();
    }

    private void customizeLegend() {
        int monthStart = Preferences.getMonthStart(getContext());
        Legend l = chart.getLegend();
        l.setEnabled(false);
        int colorCodes[] = l.getColors();
        PieData data = chart.getData();
        List<Entry> entries = ((PieDataSet) data.getDataSet()).getYVals();
        legendData = new ArrayList<>();

        for (int i = 0; i < l.getColors().length - 1; i++) {
            Entry entry = entries.get(i);
            String label = l.getLabel(i);
            int colorCode = colorCodes[i];
            float val = entry.getVal();
            float valPercent = entry.getVal() / data.getYValueSum() * 100f;
            legendData.add(new LegendDetails(colorCode, label, val, valPercent));
        }

        if (legendData.size() != 0) {
            adapter.setData(legendData);
            tableDetails.setVisibility(View.VISIBLE);
            final Interval historyInterval = DataInterval.getHistoryInterval(System.currentTimeMillis(), 1, monthStart);
            long start;
            long end = historyInterval.getEndMillis();
            if (monthStart == 1) {
                start = historyInterval.getStartMillis();
            } else {
                start = historyInterval.getStart().minusDays(1).getMillis();
            }
            chart.setDescription(TimeUtils.formatIntervalTimeString(start, end, null, getContext()));
        } else {
            tableDetails.setVisibility(View.GONE);
            chart.setDescription("");
        }
    }


    private SpannableString generateCenterText() {
        String centerText = String.format("%s\n%s", totalOut, balance);
        SpannableString s = new SpannableString(centerText);
        s.setSpan(new ForegroundColorSpan(CustomColorTemplate.OUTCOME_COLOR), 0, totalOut.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        s.setSpan(new ForegroundColorSpan(CustomColorTemplate.INCOME_COLOR), totalOut.length(), s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return s;
    }

    protected PieData generatePieData() {
        float totalAmount = 0;
        float totalBalance = 0;
        List<Integer> colors = new ArrayList<>();
        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<String> xVals = new ArrayList<>();
        HashMap<String, Float> data = new HashMap<>();

        for (Account acc : accounts) {
            totalBalance += acc.getBalance();
        }

        for (int i = 0; i < outs.size(); i++) {
            Transaction elem = outs.get(i);
            String key = elem.getCategory().getTitle();
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
                colors.add(CustomColorTemplate.getColorForCategory(getContext(), entry.getKey()));
                entries.add(new Entry(entry.getValue(), j));
                j++;
            }
        }

        String sign = CurrencyCache.getCurrencyOrEmpty().getSymbol();
        balance = String.format("%10.2f %s", totalBalance, sign).trim();
        totalOut = String.format("%10.2f %s", totalAmount, sign).trim();
        PieDataSet ds1 = new PieDataSet(entries, "");
        ds1.setColors(colors);
        ds1.setSliceSpace(2f);
        ds1.setValueTextColor(CustomColorTemplate.WHITE_TEXT);
        ds1.setValueTextSize(8f);
        ds1.setDrawValues(false);


        PieData d = new PieData(xVals, ds1);

        return d;
    }

    private PieData getEmptyData() {
        PieDataSet dataSet = new PieDataSet(null, getResources().getString(R.string.chart_no_data));
        dataSet.addEntry(new Entry(1, 0));
        dataSet.setColor(NO_DATA_COLOR);
        dataSet.setDrawValues(false);
        return new PieData(Collections.singletonList(""), dataSet);
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
        showEditDialog(i);
        // String label = chart.getXValue(i);
        //BusProvider.postOnMain(new ChartSliceClickedEvent(label));
    }

    private void showEditDialog(int position) {
        FragmentManager fm = getFragmentManager();
        LegendDetails details = legendData.get(position);
        HistoryForCategoryDialog historyCategoryDialog = HistoryForCategoryDialog.newInstance();
        ArrayList<Transaction> list = new ArrayList<>();
        for (Transaction t : outs) {
            if (t.getCategory() != null && t.getCategory().getTitle().equals(details.CategoryName)) {
                list.add(t);
            }
        }
        CommonUtils sorter = new CommonUtils();
        sorter.sortWalletEntriesByDate(list, true);
        historyCategoryDialog.setData(details, list);
        historyCategoryDialog.show(fm, "fragment_history_category");
    }

    @Override
    public void onNothingSelected() {

    }

    @Subscribe
    public void OnWalletChange(WalletChangeEvent event) {
        CheckWallet();
    }


    public void CheckWallet() {
        getDataForCurrentMonth();
        accounts = MoneyApplication.getInstance().getAccounts();
        displayChart();
    }

}
