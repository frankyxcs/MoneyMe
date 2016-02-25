package com.devmoroz.moneyme;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;


public class AlarmService extends Service {

    public static final String ACTION_TODO_START = "com.devmoroz.moneyme.intent.ACTION_TODO_START";
    public static final String ACTION_TODO_DELETE = "com.devmoroz.moneyme.intent.ACTION_TODO_DELETE";
    public static final String ACTION_REGULAR_START = "com.devmoroz.moneyme.intent.ACTION_REGULAR_START";
    public static final String ACTION_REGULAR_DELETE = "com.devmoroz.moneyme.intent.ACTION_REGULAR_DELETE";
    static final String ACTION_FASTBOOT = "com.htc.intent.action.QUICKBOOT_POWERON";

    private NotificationManager mManager;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String intentAction = intent.getAction();

        if (intentAction != null)
        {
            switch (intentAction)
            {
                case Intent.ACTION_BOOT_COMPLETED:
                case Intent.ACTION_REBOOT:
                case ACTION_FASTBOOT:

            }
        }

        final Notification note = buildNotification();
        mManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mManager.notify(startId,note);

        return START_NOT_STICKY;
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Notification buildNotification() {
        Intent notifyIntent =
                new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,0,notifyIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this);

        String text_small = getString(R.string.main_notification_content_small);
        String text_large = getString(R.string.main_notification_content_large);

        builder.setSmallIcon(R.drawable.ic_notification_moneyme)
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
        Notification note = expandedStyle.build();

        return note;
    }
}
