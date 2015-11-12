package com.devmoroz.moneyme.utils;


import android.widget.EditText;

import com.devmoroz.moneyme.models.Currency;

public class FormatUtils {

    public static String amountToString(Currency c, double amount) {
        StringBuilder sb = new StringBuilder();
        return amountToString(sb, c, amount, false).toString();
    }

    public static StringBuilder amountToString(StringBuilder sb, Currency c, double amount) {
        return amountToString(sb, c, amount, false);
    }

    public static String amountToString(Currency c, double amount, boolean addPlus) {
        StringBuilder sb = new StringBuilder();
        return amountToString(sb, c, amount, addPlus).toString();
    }

    public static StringBuilder amountToString(StringBuilder sb, Currency c, double amount, boolean addPlus) {
        if (c == null) {
            c = Currency.EMPTY;
        }
        String s = c.getFormat().format(amount);
        sb.append(s);
        if (isNotEmpty(c.getSymbol())) {
                sb.append(" ").append(c.getSymbol());

        }
        return sb;
    }

    public static boolean isNotEmpty(String s) {
        return s != null && s.length() > 0;
    }

    public static boolean isEmpty(String s) {
        return s == null || s.length() == 0;
    }

}
