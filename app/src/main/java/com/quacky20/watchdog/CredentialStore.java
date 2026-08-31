package com.quacky20.watchdog;

import android.content.Context;
import android.content.SharedPreferences;

public class CredentialStore {

    private static final String PREFS = "watchdog";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";

    private final SharedPreferences preferences;

    public CredentialStore(Context context) {
        preferences = context.getSharedPreferences(
                PREFS,
                Context.MODE_PRIVATE
        );
    }

    public void saveCredentials(
            String username,
            String password
    ) {
        preferences.edit()
                .putString(KEY_USERNAME, username)
                .putString(KEY_PASSWORD, password)
                .apply();
    }

    public String getUsername() {
        return preferences.getString(
                KEY_USERNAME,
                null
        );
    }

    public String getPassword() {
        return preferences.getString(
                KEY_PASSWORD,
                null
        );
    }

    public boolean hasCredentials() {
        return getUsername() != null
                && getPassword() != null;
    }
}