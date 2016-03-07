package com.devmoroz.moneyme;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.text.Spanned;

import com.devmoroz.moneyme.export.BackupRestoreHelper;
import com.devmoroz.moneyme.models.Todo;
import com.devmoroz.moneyme.notification.MoneyMeScheduler;
import com.devmoroz.moneyme.notification.NotificationsHelper;
import com.devmoroz.moneyme.utils.Constants;
import com.devmoroz.moneyme.utils.TextUtils;


public class AlarmService extends Service {

    public static final String ACTION_SCHEDULED_BACKUP = "com.devmoroz.moneyme.intent.ACTION_SCHEDULED_BACKUP";
    public static final String ACTION_SCHEDULE_AUTO_BACKUP = "com.devmoroz.moneyme.intent.ACTION_SCHEDULE_AUTO_BACKUP";
    public static final String ACTION_AUTO_BACKUP = "com.devmoroz.moneyme.intent.ACTION_AUTO_BACKUP";

    public static final String ACTION_SCHEDULED_TODO_ALARM = "com.devmoroz.moneyme.intent.ACTION_SCHEDULED_TODO_ALARM";
    public static final String ACTION_SCHEDULE_ALL = "com.devmoroz.moneyme.intent.ACTION_SCHEDULE_ALL";

    public static final String ACTION_DAILY_NOTIFICATION = "com.devmoroz.moneyme.intent.ACTION_DAILY_NOTIFICATION";


    private NotificationManager mManager;
    private MoneyMeScheduler scheduler;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String intentAction = intent.getAction();
        scheduler = new MoneyMeScheduler();
        if (intentAction != null) {
            switch (intentAction) {
                case ACTION_SCHEDULE_ALL:
                    scheduleAll();
                    break;
                case ACTION_SCHEDULE_AUTO_BACKUP:
                    scheduleNextAutoBackup();
                    break;
                case ACTION_AUTO_BACKUP:
                    doAutoBackup();
                    break;
                case ACTION_SCHEDULED_TODO_ALARM:
                    notifyTodo(intent);
                    break;
                case ACTION_DAILY_NOTIFICATION:
                    makeDailyNotification(startId);
                    break;
            }
        }

        return START_NOT_STICKY;
    }

    private void scheduleAll() {
        scheduler.scheduleAll(getApplicationContext());
    }

    private void scheduleNextAutoBackup() {
        scheduler.scheduleNextAutoBackup(getApplicationContext());
    }

    private void doAutoBackup() {
        try {
            BackupRestoreHelper helper = new BackupRestoreHelper(getApplicationContext());
            helper.shadowBackup();
        } finally {
            scheduleNextAutoBackup();
        }
    }

    private void notifyTodo(Intent intent) {
        Todo todoForNotify = intent.getParcelableExtra(MoneyMeScheduler.SCHEDULED_TODO_INTENT_EXTRA);
        if(todoForNotify!= null){
            createNotification(getApplicationContext(),todoForNotify);
        }
    }

    private void makeDailyNotification(int startId){
        final Notification note = buildDailyNotification(getApplicationContext());
        mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mManager.notify(startId, note);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Notification buildDailyNotification(Context context) {
        Intent notifyIntent =
                new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this);

        String text_small = getString(R.string.main_notification_content_small);
        String text_large = getString(R.string.main_notification_content_large);

        builder.setSmallIcon(R.drawable.ic_notification_moneyme)
                .setColor(context.getResources().getColor(R.color.colorPrimary))
                .setTicker("MoneyMe")
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setContentTitle("MoneyMe")
                .setContentText(text_small)
                .setContentIntent(contentIntent);

        //Apply an expanded style
        NotificationCompat.BigTextStyle expandedStyle =
                new NotificationCompat.BigTextStyle(builder);
        expandedStyle.bigText(text_large);

        return expandedStyle.build();
    }

    private void createNotification(Context mContext, Todo todo) {


        // Prepare text contents
        String title = todo.getTitle();
        String text = TextUtils.parseContent(todo);

        Intent snoozeIntent = new Intent(mContext, SnoozeActivity.class);
        snoozeIntent.setAction(Constants.ACTION_SNOOZE);
        snoozeIntent.putExtra(Constants.EXTRA_TODO_ITEM, todo);
        PendingIntent piSnooze = PendingIntent.getActivity(mContext, todo.getAlarm_id(), snoozeIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        String snoozeDelay = "5";

        // Next create the bundle and initialize it
        Intent intent = new Intent(mContext, TodoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.EXTRA_TODO_ITEM, todo);
        intent.putExtras(bundle);

        // Sets the Activity to start in a new, empty task
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // Workaround to fix problems with multiple notifications
        intent.setAction(Constants.ACTION_NOTIFICATION_CLICK + Long.toString(System.currentTimeMillis()));

        // Creates the PendingIntent
        PendingIntent notifyIntent = PendingIntent.getActivity(mContext, todo.getAlarm_id(), intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationsHelper notificationsHelper = new NotificationsHelper(mContext);
        notificationsHelper.createNotification(R.drawable.ic_notification_moneyme, title, notifyIntent).setLedActive()
                .setMessage(text);

        notificationsHelper.getBuilder()
                .addAction(R.drawable.ic_alarm_white_24dp, TextUtils
                        .capitalize(mContext.getString(R.string.snooze)) + ": " + snoozeDelay + mContext.getString(R.string.minute), piSnooze);

        notificationsHelper.show(todo.getAlarm_id());
    }
}
