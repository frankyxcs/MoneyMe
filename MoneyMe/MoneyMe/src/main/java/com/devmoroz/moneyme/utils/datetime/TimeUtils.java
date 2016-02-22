package com.devmoroz.moneyme.utils.datetime;


import android.content.Context;
import android.text.format.DateUtils;

import com.devmoroz.moneyme.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.TimeZone;

public class TimeUtils {

    private static final int TIME_FLAGS = DateUtils.FORMAT_ABBREV_ALL
            | DateUtils.FORMAT_SHOW_DATE;

    public static String formatShortTime(Context context, Date time) {
        // Android DateFormatter will honor the user's current settings.
        DateFormat format = android.text.format.DateFormat.getTimeFormat(context);
        // Override with Timezone based on settings since users can override their phone's timezone
        // with Pacific time zones.
        TimeZone tz = TimeZone.getDefault();
        if (tz != null) {
            format.setTimeZone(tz);
        }
        return format.format(time);
    }

    public static String formatShortTime(Context context, long date) {
        Date time = new Date(date);
        return formatShortTime(context, time);
    }

    public static String formatShortDate(Context context, long date) {
        Date dateTime = new Date(date);
        return formatShortDate(context,dateTime);
    }

    public static String formatShortDate(Context context, Date date) {
        StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter(sb);

        Calendar cal = Calendar.getInstance();
        int yearCurrent = cal.get(Calendar.YEAR);
        cal.setTime(date);
        int yearDate = cal.get(Calendar.YEAR);
        if (yearCurrent == yearDate)
            return DateUtils.formatDateRange(context, formatter, date.getTime(), date.getTime(),
                    DateUtils.FORMAT_ABBREV_ALL | DateUtils.FORMAT_NO_YEAR,
                    TimeZone.getDefault().getID()).toString();
        else
            return DateUtils.formatDateRange(context, formatter, date.getTime(), date.getTime(),
                    DateUtils.FORMAT_ABBREV_ALL,
                    TimeZone.getDefault().getID()).toString();

    }

    public static String formatHumanFriendlyShortDate(final Context context, long timestamp) {
        long localTimestamp, localTime;
        long now = System.currentTimeMillis();

        TimeZone tz = TimeZone.getDefault();
        localTimestamp = timestamp + tz.getOffset(timestamp);
        localTime = now + tz.getOffset(now);

        long dayOrd = localTimestamp / 86400000L;
        long nowOrd = localTime / 86400000L;

        if (dayOrd == nowOrd) {
            return context.getString(R.string.day_title_today);
        } else if (dayOrd == nowOrd - 1) {
            return context.getString(R.string.day_title_yesterday);
        } else if (dayOrd == nowOrd + 1) {
            return context.getString(R.string.day_title_tomorrow);
        } else {
            return formatShortDate(context, new Date(timestamp));
        }
    }

    public static String formatWidgetDateRange(long intervalStart, long intervalEnd, StringBuilder recycle,
                                               Context context) {
        return formatWidgetDateRange(intervalStart, intervalEnd, recycle, context, false);
    }

    public static String formatWidgetDateRange(long intervalStart, long intervalEnd,StringBuilder recycle,
                                               Context context, boolean shortFormat) {
        if (shortFormat) {
            TimeZone timeZone = TimeZone.getDefault();
            Date intervalStartDate = new Date(intervalStart);
            SimpleDateFormat shortDateFormat = new SimpleDateFormat("MMM dd");
            DateFormat shortTimeFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
            shortDateFormat.setTimeZone(timeZone);
            shortTimeFormat.setTimeZone(timeZone);
            return shortDateFormat.format(intervalStartDate) + " "
                    + shortTimeFormat.format(intervalStartDate);
        } else {
            String timeInterval = formatIntervalTimeString(intervalStart, intervalEnd, recycle,
                    context);
            return timeInterval;
        }
    }

    public static String formatIntervalTimeString(long intervalStart, long intervalEnd,
                                                  StringBuilder recycle, Context context) {
        if (recycle == null) {
            recycle = new StringBuilder();
        } else {
            recycle.setLength(0);
        }
        Formatter formatter = new Formatter(recycle);
        return DateUtils.formatDateRange(context, formatter, intervalStart, intervalEnd, TIME_FLAGS,
                TimeZone.getDefault().getID()).toString();
    }

    public static String formatIntervalTimeString(long intervalStart, StringBuilder recycle, Context context) {
        return formatIntervalTimeString(intervalStart, getCurrentTime(), recycle, context);
    }

    public static long getCurrentTime() {
       return System.currentTimeMillis();
    }

    public static boolean isSameDayDisplay(long time1, long time2) {
        TimeZone displayTimeZone = TimeZone.getDefault();
        Calendar cal1 = Calendar.getInstance(displayTimeZone);
        Calendar cal2 = Calendar.getInstance(displayTimeZone);
        cal1.setTimeInMillis(time1);
        cal2.setTimeInMillis(time2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    public static int getHour(String time) {
        String[] pieces=time.split(":");

        return(Integer.parseInt(pieces[0]));
    }

    public static int getMinute(String time) {
        String[] pieces=time.split(":");

        return(Integer.parseInt(pieces[1]));
    }
}
