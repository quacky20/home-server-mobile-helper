package com.quacky20.watchdog;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

public class AuthService {

    private static final String TAG = "WatchdogAuth";

    private static final String NETACCESS_URL =
            "https://netaccess.iitism.ac.in:6082/?url=";

    private final Context context;
    private final CredentialStore credentialStore;

    public AuthService(Context context) {

        this.context =
                context.getApplicationContext();

        this.credentialStore =
                new CredentialStore(context);
    }

    public boolean authenticate() {

        if (!credentialStore.hasCredentials()) {

            Log.e(
                    TAG,
                    "No credentials saved"
            );

            return false;
        }

        String username =
                credentialStore.getUsername();

        String password =
                credentialStore.getPassword();

        Log.d(
                TAG,
                "Attempting NetAccess authentication"
        );

        try {

            NetworkClient networkClient =
                    new NetworkClient(context);

            HttpsURLConnection connection =
                    networkClient.openConnection(
                            NETACCESS_URL
                    );

            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            connection.setConnectTimeout(15000);
            connection.setReadTimeout(15000);

            connection.setRequestProperty(
                    "Content-Type",
                    "application/x-www-form-urlencoded"
            );

            String data =
                    "username=" +
                            URLEncoder.encode(
                                    username,
                                    "UTF-8"
                            ) +
                            "&password=" +
                            URLEncoder.encode(
                                    password,
                                    "UTF-8"
                            );

            OutputStream output =
                    connection.getOutputStream();

            output.write(
                    data.getBytes("UTF-8")
            );

            output.flush();
            output.close();

            int responseCode =
                    connection.getResponseCode();

            Log.d(
                    TAG,
                    "Login Set-Cookie: " +
                            connection.getHeaderField("Set-Cookie")
            );

            String response =
                    readResponse(connection);

            connection.disconnect();

            Log.d(
                    TAG,
                    "Login response: HTTP " +
                            responseCode
            );

            if (responseCode != 200) {
                return false;
            }

            /*
             * The NetAccess portal currently returns
             * this page after successful authentication.
             */
            if (!response.contains(
                    "<title>Access Granted")
            ) {

                Log.e(
                        TAG,
                        "Authentication response did not " +
                                "contain Access Granted"
                );

                return false;
            }

            Log.d(
                    TAG,
                    "Authentication successful"
            );

            return true;

        } catch (Exception e) {

            Log.e(
                    TAG,
                    "Authentication failed",
                    e
            );

            return false;
        }
    }

    private String readResponse(
            HttpURLConnection connection
    ) throws Exception {

        InputStream input;

        if (connection.getResponseCode() >= 400) {
            input = connection.getErrorStream();
        } else {
            input = connection.getInputStream();
        }

        BufferedReader reader =
                new BufferedReader(
                        new InputStreamReader(input)
                );

        StringBuilder result =
                new StringBuilder();

        String line;

        while ((line = reader.readLine()) != null) {
            result.append(line).append("\n");
        }

        reader.close();

        return result.toString();
    }

    public boolean isAuthenticated() {

        Log.d(
                TAG,
                "Checking NetAccess authentication"
        );

        try {

            NetworkClient networkClient =
                    new NetworkClient(context);

            HttpsURLConnection connection =
                    networkClient.openConnection(
                            "https://netaccess.iitism.ac.in:6082/"
                    );

            connection.setRequestMethod("GET");

            connection.setConnectTimeout(15000);
            connection.setReadTimeout(15000);

            int responseCode =
                    connection.getResponseCode();

            String response =
                    readResponse(connection);

            Log.d(
                    TAG,
                    "Check response: " +
                            response.substring(
                                    0,
                                    Math.min(response.length(), 1000)
                            )
            );

            connection.disconnect();

            Log.d(
                    TAG,
                    "Authentication check: HTTP " +
                            responseCode
            );

            if (responseCode != 200) {
                return false;
            }

            boolean authenticated =
                    response.contains("Already Connected — IIT(ISM)") ||
                            response.contains("Access Granted — IIT(ISM)");

            Log.d(
                    TAG,
                    "Authenticated: " +
                            authenticated
            );

            return authenticated;

        } catch (Exception e) {

            Log.e(
                    TAG,
                    "Authentication check failed",
                    e
            );

            return false;
        }
    }
}