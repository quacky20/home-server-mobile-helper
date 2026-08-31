package com.quacky20.watchdog;

import android.content.Context;
import android.util.Log;

public class AuthService {

    private static final String TAG = "WatchdogAuth";

    private final Context context;
    private final CredentialStore credentialStore;

    public AuthService(Context context) {
        this.context = context.getApplicationContext();
        this.credentialStore =
                new CredentialStore(context);
    }

    public boolean authenticate() {

        if (!credentialStore.hasCredentials()) {
            Log.e(TAG, "No credentials saved");
            return false;
        }

        String username =
                credentialStore.getUsername();

        String password =
                credentialStore.getPassword();

        Log.d(TAG, "Attempting NetAccess authentication");

        // We'll move the working HTTPS login code
        // into here next.

        return false;
    }
}