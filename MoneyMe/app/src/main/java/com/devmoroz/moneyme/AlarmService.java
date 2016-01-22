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

    private NotificationManager mManager;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

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

        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("Something Happened")
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setContentTitle("MoneyMe")
                .setContentText("Click Here!")
                .setContentIntent(contentIntent);

        //Apply an expanded style
        NotificationCompat.BigTextStyle expandedStyle =
                new NotificationCompat.BigTextStyle(builder);
        expandedStyle.bigText("Here is some additional text to be displayed when"
                + " the notification is in expanded mode. "
                + " I can fit so much more content into this giant view!");
        Notification note = expandedStyle.build();

        return note;
    }
}
