package com.devmoroz.moneyme.notification;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.devmoroz.moneyme.AlarmService;
import com.devmoroz.moneyme.MoneyApplication;
import com.devmoroz.moneyme.helpers.DBHelper;
import com.devmoroz.moneyme.models.Todo;
import com.devmoroz.moneyme.utils.FormatUtils;
import com.devmoroz.moneyme.utils.Preferences;
import com.devmoroz.moneyme.utils.datetime.TimeUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MoneyMeScheduler {

    public static final String SCHEDULED_TODO_INTENT_EXTRA = "scheduledTodoIntentExtra";

    private DBHelper dbHelper;

    public void scheduleAll(Context context) {
        long now = System.currentTimeMillis() + 1000;

        scheduleAllTodo(context, now);
        scheduleDailyAlarm(context);
    }

    public void scheduleAllTodo(Context context, long now) {
        List<Todo> scheduled = getSortedSchedules(now);
        for (Todo todo : scheduled) {
            scheduleTodoAlarm(context, todo, now);
        }
    }

    public boolean scheduleTodoAlarm(Context context, Todo todo, long now) {
        if (shouldSchedule(todo, now)) {
            Date scheduleTime = todo.getAlarmDate();
            AlarmManager service = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            PendingIntent pendingIntent = createPendingIntentForScheduledTodoAlarm(context, todo);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                service.setExact(AlarmManager.RTC_WAKEUP, scheduleTime.getTime(), pendingIntent);
            } else {
                service.set(AlarmManager.RTC_WAKEUP, scheduleTime.getTime(), pendingIntent);
            }

            return true;
        }
        return false;
    }

    public void removeReminder(Context context, Todo todo) {
        if (todo.getAlarmDate() != null) {
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            PendingIntent p = createPendingIntentForScheduledTodoAlarm(context, todo);
            am.cancel(p);
            p.cancel();
        }
    }

    private boolean shouldSchedule(Todo todo, long now) {
        return todo.getAlarmDate() != null && now < todo.getAlarmDateLong();
    }

    private PendingIntent createPendingIntentForScheduledTodoAlarm(Context context, Todo todo) {
        Intent intent = new Intent(AlarmService.ACTION_SCHEDULED_TODO_ALARM, null, context, AlarmService.class);
        intent.putExtra(SCHEDULED_TODO_INTENT_EXTRA, todo);
        return PendingIntent.getService(context, todo.getAlarm_id(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    public void cancelPendingIntentForTodoSchedule(Context context, Todo todo) {
        AlarmManager service = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent intent = createPendingIntentForScheduledTodoAlarm(context, todo);
        service.cancel(intent);
    }

    private List<Todo> getSortedSchedules(long now) {
        List<Todo> needSchedule = Collections.emptyList();
        try {
            dbHelper = MoneyApplication.GetDBHelper();
            needSchedule = dbHelper.getTodoDAO().queryForAlarm(now);
        } catch (SQLException ex) {

        }
        return needSchedule;
    }


    public static void scheduleDailyAlarm(Context context) {

            Intent startServiceIntent = new Intent(AlarmService.ACTION_DAILY_NOTIFICATION, null, context, AlarmService.class);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            PendingIntent pendingIntent = PendingIntent.getService(context, 12345, startServiceIntent, PendingIntent.FLAG_CANCEL_CURRENT);

            int hour = 18;
            int minute = 0;

            String time = Preferences.getNotificationTime(context);
            if (FormatUtils.isNotEmpty(time)) {
                hour = TimeUtils.getHour(time);
                minute = TimeUtils.getMinute(time);
            }

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 00);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 24 * 60 * 60 * 1000, pendingIntent);

    }

    public static void cancelDailyAlarm(Context context) {
        Intent startServiceIntent = new Intent(AlarmService.ACTION_DAILY_NOTIFICATION, null, context, AlarmService.class);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getService(context, 12345, startServiceIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
    }

    public void scheduleNextAutoBackup(Context context) {
        if (Preferences.isAutoBackupEnabled(context)) {
            int f = Preferences.getAutoBackupFrequency(context);

            scheduleBackup(context, f);
        }
    }

    private PendingIntent createPendingIntent(Context context) {
        Intent intent = new Intent(AlarmService.ACTION_SCHEDULED_BACKUP, null, context, AlarmService.class);
        return PendingIntent.getService(context, -123456, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    private void scheduleBackup(Context context, int frequency) {
        AlarmManager service = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = createPendingIntent(context);
        Date scheduledTime = getBackupScheduledTime(frequency, System.currentTimeMillis());
        service.set(AlarmManager.RTC_WAKEUP, scheduledTime.getTime(), pendingIntent);
    }

    public Date getBackupScheduledTime(int frequency, long now) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(now);
        c.set(Calendar.HOUR_OF_DAY, 6);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        if (frequency != 31) {
            c.add(Calendar.DAY_OF_MONTH, frequency);
        } else {
            c.add(Calendar.MONTH, 1);
        }

        return c.getTime();
    }
}
