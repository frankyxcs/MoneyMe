package com.devmoroz.moneyme.utils.datetime;


import android.content.Context;
import android.text.format.DateUtils;

import com.devmoroz.moneyme.R;

import java.text.DateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.TimeZone;

public class TimeUtils {

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

    public static String formatShortDate(Context context, Date date) {
        StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter(sb);
        
        return DateUtils.formatDateRange(context, formatter, date.getTime(), date.getTime(),
                DateUtils.FORMAT_ABBREV_ALL | DateUtils.FORMAT_NO_YEAR,
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
}
