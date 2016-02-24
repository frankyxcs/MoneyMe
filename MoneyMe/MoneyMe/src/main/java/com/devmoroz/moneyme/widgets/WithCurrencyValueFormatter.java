package com.devmoroz.moneyme.widgets;

import android.content.Context;

import com.devmoroz.moneyme.utils.FormatUtils;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;


public class WithCurrencyValueFormatter implements ValueFormatter {

    private DecimalFormat mFormat;
    private String sign;

    public WithCurrencyValueFormatter(String sign) {
        mFormat = new DecimalFormat("###,###,###,##0.0");
        this.sign = sign;
    }

    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        if(value == 0){
            return "";
        }
        if (FormatUtils.isNotEmpty(sign)) {
            return mFormat.format(value) + " " + sign;
        }
        return mFormat.format(value);
    }
}
