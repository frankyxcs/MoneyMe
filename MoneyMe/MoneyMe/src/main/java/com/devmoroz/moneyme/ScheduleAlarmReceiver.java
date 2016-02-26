package com.devmoroz.moneyme;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.devmoroz.moneyme.notification.NotificationUpdaterService;
import com.devmoroz.moneyme.utils.FormatUtils;
import com.devmoroz.moneyme.utils.Preferences;
import com.devmoroz.moneyme.utils.datetime.TimeUtils;

import java.util.Calendar;


public class ScheduleAlarmReceiver extends BroadcastReceiver {

    static final String ACTION_FASTBOOT = "com.htc.intent.action.QUICKBOOT_POWERON";


    @Override
    public void onReceive(Context context, Intent intent) {
        String intentAction = intent.getAction();

        if (intentAction != null)
        {
            switch (intentAction)
            {
                case Intent.ACTION_BOOT_COMPLETED:
                case Intent.ACTION_REBOOT:
                case ACTION_FASTBOOT:
                    requestScheduleAll(context);
                    requestScheduleAutoBackup(context);
                case Intent.ACTION_DATE_CHANGED:
                case Intent.ACTION_TIME_CHANGED:
                case Intent.ACTION_TIMEZONE_CHANGED:
                    requestUpdateAll(context);
                case AlarmService.ACTION_SCHEDULED_BACKUP:
                    requestAutoBackup(context);

            }
        }

    }

    private void requestScheduleAll(Context context) {
        Intent serviceIntent = new Intent(AlarmService.ACTION_SCHEDULE_ALL, null, context, AlarmService.class);
        context.startService(serviceIntent);
    }

    private void requestScheduleAutoBackup(Context context) {
        Intent serviceIntent = new Intent(AlarmService.ACTION_SCHEDULE_AUTO_BACKUP, null, context, AlarmService.class);
        context.startService(serviceIntent);
    }

    private void requestUpdateAll(Context context) {
       // Intent serviceIntent = new Intent(AlarmService.ACTION_SCHEDULE_ALL, null, context, AlarmService.class);
       // context.startService(serviceIntent);
    }

    private void requestAutoBackup(Context context) {
        Intent serviceIntent = new Intent(AlarmService.ACTION_AUTO_BACKUP, null, context, AlarmService.class);
        context.startService(serviceIntent);
    }

}
