package com.devmoroz.moneyme;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.devmoroz.moneyme.utils.FormatUtils;
import com.devmoroz.moneyme.utils.Preferences;
import com.devmoroz.moneyme.utils.datetime.TimeUtils;

import java.util.Calendar;


public class StartOnBoot extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        setAlarm(context);
    }

    public static void setAlarm(Context context){
        Intent startServiceIntent = new Intent(context, AlarmService.class);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, startServiceIntent, 0);

        int hour = 18;
        int minute = 0;

        String time = Preferences.getNotificationTime(context);
        if(FormatUtils.isNotEmpty(time)){
            hour = TimeUtils.getHour(time);
            minute = TimeUtils.getMinute(time);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 00);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 24*60*60*1000 , pendingIntent);
    }

    public static void cancelAlarm(Context context){
        Intent startServiceIntent = new Intent(context, AlarmService.class);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, startServiceIntent, 0);

        alarmManager.cancel(pendingIntent);
    }
}
