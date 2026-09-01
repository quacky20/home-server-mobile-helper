package com.quacky20.watchdog;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class WatchdogService extends Service {

    private static final String TAG = "Watchdog";

    private static final long INITIAL_DELAY =
            30000; // 30 seconds

    private static final long CHECK_INTERVAL =
            5 * 60 * 1000; // 5 minutes

    private Thread watchdogThread;
    private volatile boolean running = true;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "Watchdog service started");

        watchdogThread = new Thread(
                new Runnable() {
                    @Override
                    public void run() {

                        // Give Android/Wi-Fi time to settle.
                        sleep(INITIAL_DELAY);

                        Log.d(
                                TAG,
                                "Watchdog initial check"
                        );

                        while (running) {

                            performCheck();

                            sleep(CHECK_INTERVAL);
                        }

                        Log.d(
                                TAG,
                                "Watchdog thread stopped"
                        );
                    }
                }
        );

        watchdogThread.start();
    }

    private void performCheck() {

        Log.d(
                TAG,
                "Performing authentication check"
        );

        try {

            AuthService authService =
                    new AuthService(this);

            if (authService.isAuthenticated()) {

                Log.d(
                        TAG,
                        "Internet access is active"
                );

                return;
            }

            Log.d(
                    TAG,
                    "Not authenticated. " +
                            "Attempting login..."
            );

            boolean success =
                    authService.authenticate();

            if (success) {

                Log.d(
                        TAG,
                        "Authentication successful"
                );

            } else {

                Log.e(
                        TAG,
                        "Authentication failed"
                );
            }

        } catch (Exception e) {

            Log.e(
                    TAG,
                    "Watchdog check failed",
                    e
            );
        }
    }

    private void sleep(long milliseconds) {

        try {

            Thread.sleep(milliseconds);

        } catch (InterruptedException e) {

            Log.d(
                    TAG,
                    "Watchdog sleep interrupted"
            );

            Thread.currentThread().interrupt();
        }
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
    public void onDestroy() {

        running = false;

        if (watchdogThread != null) {
            watchdogThread.interrupt();
        }

        Log.d(
                TAG,
                "Watchdog service destroyed"
        );

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}