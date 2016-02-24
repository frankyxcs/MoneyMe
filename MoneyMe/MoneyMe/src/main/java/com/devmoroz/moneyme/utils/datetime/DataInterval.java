package com.devmoroz.moneyme.utils.datetime;


import android.content.Context;

import com.devmoroz.moneyme.R;

import net.danlew.android.joda.DateUtils;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Period;

public class DataInterval {

    protected final Context context;
    protected final int length = 1;

    protected Type type;


    protected Interval interval;

    public DataInterval(Context context, Type type) {
        this.context = context;
        this.type = type;
        reset();
    }

    public void setType(Type type) {
        this.type = type;
        reset();
    }

    public static Period getPeriod(Type type, int length) {
        final Period period;
        switch (type) {
            case WEEK:
                period = Period.weeks(length);
                break;
            case MONTH:
                period = Period.months(length);
                break;
            case YEAR:
                period = Period.years(length);
                break;
            default:
                throw new IllegalArgumentException("Type " + type + " is not supported.");
        }
        return period;
    }

    public static Period getSubPeriod(Type type, int length) {
        final Period period;
        switch (type) {
            case WEEK:
                period = Period.days(length);
                break;
            case MONTH:
                period = Period.days(length);
                break;
            case YEAR:
                period = Period.months(length);
                break;
            default:
                throw new IllegalArgumentException("Type " + type + " is not supported.");
        }
        return period;
    }

    public static Interval getInterval(long millis, Period period, Type type) {
        final DateTime currentTime = new DateTime(millis);
        final DateTime intervalStart;
        switch (type) {
            case WEEK:
                intervalStart = currentTime.weekOfWeekyear().roundFloorCopy();
                break;
            case MONTH:
                intervalStart = currentTime.dayOfMonth().withMinimumValue().withTimeAtStartOfDay();
                break;
            case YEAR:
                intervalStart = currentTime.withMonthOfYear(1).withDayOfMonth(1).withTimeAtStartOfDay();
                break;
            default:
                throw new IllegalArgumentException("Type " + type + " is not supported.");
        }

        return new Interval(intervalStart, period);
    }

    public static String getTitle(Context context, Interval interval, Type type) {
        switch (type) {
            case WEEK:
                return DateUtils.formatDateTime(context, interval.getStart(), DateUtils.FORMAT_ABBREV_ALL) + " - " + DateUtils.formatDateTime(context, interval.getEnd().minusMillis(1), DateUtils.FORMAT_ABBREV_ALL);
            case MONTH:
                return DateUtils.formatDateTime(context, interval.getStart(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NO_MONTH_DAY);
            case YEAR:
                return interval.getStart().year().getAsText();
            default:
                throw new IllegalArgumentException("Type " + type + " is not supported.");
        }
    }

    public static String getSubTypeShortestTitle(Interval interval, Type type) {
        switch (type) {
            case WEEK:
                return interval.getStart().dayOfWeek().getAsShortText();
            case MONTH:
                return interval.getStart().dayOfMonth().getAsShortText();
            case YEAR:
                return interval.getStart().monthOfYear().getAsShortText();
            default:
                throw new IllegalArgumentException("Type " + type + " is not supported.");
        }
    }

    public String getTypeTitle() {
        switch (type) {
            case WEEK:
                return context.getString(R.string.period_week);
            case MONTH:
                return context.getString(R.string.period_month);
            case YEAR:
                return context.getString(R.string.period_year);
            default:
                throw new IllegalArgumentException("Type " + type + " is not supported.");
        }
    }

    public Type getType() {
        return type;
    }

    public int getLength() {
        return length;
    }

    public Interval getInterval() {
        return interval;
    }

    public String getTitle() {
        return getTitle(context, interval, type);
    }

    protected Period getPeriodForType() {
        return getPeriod(type, length);
    }

    public void reset() {
        interval = getInterval(System.currentTimeMillis(), getPeriodForType(), type);
    }

    public static enum Type {
        WEEK, MONTH, YEAR
    }
}
