package com.devmoroz.moneyme;


import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.devmoroz.moneyme.models.Todo;
import com.devmoroz.moneyme.notification.MoneyMeScheduler;
import com.devmoroz.moneyme.utils.Constants;
import com.devmoroz.moneyme.utils.datetime.TimeUtils;

import java.util.Calendar;

public class SnoozeActivity extends AppCompatActivity {

    private Todo todo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getParcelableExtra(Constants.EXTRA_TODO_ITEM) != null) {
            todo = getIntent().getParcelableExtra(Constants.EXTRA_TODO_ITEM);
            manageNotification();
        }
    }

    private void manageNotification() {
        if (Constants.ACTION_SNOOZE.equals(getIntent().getAction())) {
            String snoozeDelay = "5";
            long newReminder = Calendar.getInstance().getTimeInMillis() + Integer.parseInt(snoozeDelay) * 60 * 1000;
            updateTodoReminder(newReminder, todo);
            finish();
        }
        removeNotification(todo);
    }

    private void updateTodoReminder(long reminder, Todo todoToUpdate) {
        MoneyMeScheduler scheduler = new MoneyMeScheduler();
        scheduler.scheduleTodoAlarm(MoneyApplication.getAppContext(), todoToUpdate, reminder);
        showReminderMessage(reminder);

    }

    public void showReminderMessage(long reminder) {
        if (reminder > Calendar.getInstance().getTimeInMillis()) {
            new Handler(MoneyApplication.getAppContext().getMainLooper()).post(() -> Toast.makeText(MoneyApplication
                            .getAppContext(),
                    MoneyApplication.getAppContext().getString(R.string.alarm_reset_on) + " " + TimeUtils.formatHumanFriendlyShortDateTime
                            (MoneyApplication.getAppContext(), reminder), Toast.LENGTH_LONG).show());
        }

    }

    private void removeNotification(Todo todo) {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(String.valueOf(todo.getAlarm_id()), 0);
    }
}
