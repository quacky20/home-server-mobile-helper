package com.quacky20.watchdog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {

    private static final String TAG = "Watchdog";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {

            Log.d(TAG, "Boot completed");

            Intent serviceIntent =
                    new Intent(context, WatchdogService.class);

            context.startService(serviceIntent);
        }
    }
}