package com.quacky20.watchdog;

import android.app.Application;

import java.net.CookieHandler;
import java.net.CookieManager;

public class WatchdogApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        CookieManager cookieManager =
                new CookieManager();

        CookieHandler.setDefault(cookieManager);
    }
}