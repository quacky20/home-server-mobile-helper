package com.quacky20.watchdog;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class WatchdogService extends Service {

    private static final String TAG = "Watchdog";

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "Watchdog service started");

        new Thread(new Runnable() {
            @Override
            public void run() {

                // Give Android/Wi-Fi some time to settle after boot.
                try {
                    Thread.sleep(30000);
                } catch (InterruptedException ignored) {
                }

                Log.d(TAG, "Watchdog initial check");

                // We'll put our connectivity/authentication
                // logic here next.
            }
        }).start();
    }

    @Override
    public int onStartCommand(
            Intent intent,
            int flags,
            int startId
    ) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}