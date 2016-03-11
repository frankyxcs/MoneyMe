package com.devmoroz.moneyme.utils;


import android.content.Context;
import android.support.annotation.ColorRes;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.EditText;
import android.widget.TextView;

import com.devmoroz.moneyme.R;
import com.devmoroz.moneyme.models.Currency;
import com.devmoroz.moneyme.models.Transaction;

public class FormatUtils {

    private static final String TRANSFER_SYMBOL = " → ";
    private static final String UNKNOWN_VALUE = "?";

    private final StringBuilder sb = new StringBuilder();

    private final Context context;


    public int zeroColor = R.color.zero_amount;

    public int positiveColor =R.color.positive_amount;

    public int negativeColor = R.color.negative_amount;

    public FormatUtils(Context context) {
        this.context = context;
    }

    public static String amountToString(Currency c, double amount) {
        StringBuilder sb = new StringBuilder();
        return amountToString(sb, c, amount, false).toString();
    }

    public static StringBuilder amountToString(StringBuilder sb, Currency c, double amount) {
        return amountToString(sb, c, amount, false);
    }

    public static String amountToString(Currency c, double amount, boolean addMinus) {
        StringBuilder sb = new StringBuilder();
        return amountToString(sb, c, amount, addMinus).toString();
    }

    public static StringBuilder amountToString(StringBuilder sb, Currency c, double amount, boolean addMinus) {
        if (c == null) {
            c = Currency.EMPTY;
        }
        if (addMinus && amount < 0) {
            sb.append("-");
        }
        String s = c.getFormat().format(amount);
        sb.append(s);
        if (isNotEmpty(c.getSymbol())) {
                sb.append(" ").append(c.getSymbol());

        }
        return sb;
    }

    public static String goalProgressToString(Currency c, int accumulated, int needed) {
        StringBuilder sb = new StringBuilder();
        if (c == null) {
            c = Currency.EMPTY;
        }
        String a = c.getFormat().format(accumulated);
        sb.append(a);
        if (isNotEmpty(c.getSymbol())) {
            sb.append(" ").append(c.getSymbol());
        }
        sb.append("/");
        String n = c.getFormat().format(needed);
        sb.append(n);
        if (isNotEmpty(c.getSymbol())) {
            sb.append(" ").append(c.getSymbol());
        }
        return sb.toString();
    }

    public void setAmountText(TextView view, Currency c, double amount, boolean addPlus) {
        setAmountText(new StringBuilder(), view, c, amount, addPlus);
    }

    public void setAmountText(StringBuilder sb, TextView view, Currency c, double amount, boolean addPlus) {
        view.setText(amountToString(sb, c, amount, addPlus).toString());
        view.setTextColor(amount == 0 ? context.getResources().getColor(zeroColor) : (amount > 0 ? context.getResources().getColor(positiveColor) : context.getResources().getColor(negativeColor)));
    }

    public static String attachAmountToText(String s, Currency currency, double amount, boolean addMinus) {
        StringBuilder sb = new StringBuilder();
        sb.append(s + " [");
        amountToString(sb, currency, amount, addMinus);
        sb.append("]");
        return sb.toString();
    }

    public static String attachAmountToTextWithoutBrackets(String s, Currency currency, double amount, boolean addMinus) {
        StringBuilder sb = new StringBuilder();
        sb.append(s + ": ");
        amountToString(sb, currency, amount, addMinus);
        return sb.toString();
    }

    public static String roundValueToString(float value){
        long rounded = Math.round(value);
        return String.valueOf(rounded);
    }

    public static String roundValueToStringWithPercent(float value){
        long rounded = Math.round(value);
        return String.valueOf(rounded) + "%";
    }

    public static boolean isNotEmpty(String s) {
        return s != null && s.length() > 0;
    }

    public static boolean isEmpty(String s) {
        return s == null || s.length() == 0;
    }

    public static boolean isEmpty(EditText e) {
        return isEmpty(text(e));
    }

    public static String text(EditText text) {
        String s = text.getText().toString().trim();
        return s.length() > 0 ? s : null;
    }

    public static boolean sameCurrency(Currency fromCurrency, Currency toCurrency) {
        return fromCurrency.getId() == toCurrency.getId();
    }

    public static void setTextWithLinks(TextView textView, String htmlText) {
        setHtmlText(textView, htmlText);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public static void setHtmlText(TextView textView, String htmlText) {
        textView.setText(isEmpty(htmlText) ? null : Html.fromHtml(htmlText));
    }

    public static void setTextViewText(TextView textView, String text) {
        textView.setText(isEmpty(text) ? null : Html.fromHtml(text));
    }

    public static String getAccountTitle(Transaction transaction) {
        switch (transaction.getType()) {
            case OUTCOME:
                return transaction.getAccountFrom() != null ? transaction.getAccountFrom().getName() : UNKNOWN_VALUE;
            case INCOME:
                return transaction.getAccountTo() != null ? transaction.getAccountTo().getName() : UNKNOWN_VALUE;
            case TRANSFER:
                return (transaction.getAccountFrom() != null ? transaction.getAccountFrom().getName() : UNKNOWN_VALUE) + TRANSFER_SYMBOL + (transaction.getAccountTo() != null ? transaction.getAccountTo().getName() : UNKNOWN_VALUE);
            default:
                throw new IllegalArgumentException("Transaction type " + transaction.getType() + " is not supported.");
        }
    }

    public static String getTransactionTitle(Context context,Transaction transaction) {
        switch (transaction.getType()) {
            case OUTCOME:
                return transaction.getCategory() != null ? transaction.getCategory().getTitle() : UNKNOWN_VALUE;
            case INCOME:
                return context.getString(R.string.income_toolbar_name);
            case TRANSFER:
                return context.getString(R.string.transfer_toolbar_name);
            default:
                throw new IllegalArgumentException("Transaction type " + transaction.getType() + " is not supported.");
        }
    }

}
