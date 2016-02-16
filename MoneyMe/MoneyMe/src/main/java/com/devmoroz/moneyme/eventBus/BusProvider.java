package com.devmoroz.moneyme.eventBus;

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;


public class BusProvider {

    private static final Handler mainThread = new Handler(Looper.getMainLooper());

    public static final Bus BUS = new Bus();

    public static Bus getInstance() {
        return BUS;
    }

    public static void postOnMain(final Object event) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            getInstance().post(event);
        } else {
            mainThread.post(new Runnable() {
                @Override
                public void run() {
                    getInstance().post(event);

                }
            });
        }
    }

    private BusProvider() {

    }
}
