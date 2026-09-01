package com.quacky20.watchdog;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

public class MainActivity extends Activity {

    private EditText usernameInput;
    private EditText passwordInput;
    private TextView statusText;
    private Button authenticateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        usernameInput = (EditText) findViewById(R.id.username);
        passwordInput = (EditText) findViewById(R.id.password);
        statusText = (TextView) findViewById(R.id.status);
        authenticateButton = (Button) findViewById(R.id.authenticate);

        authenticateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                authenticate();
            }
        });
    }

    private void authenticate() {

        final String username =
                usernameInput.getText().toString().trim();

        final String password =
                passwordInput.getText().toString();

        if (username.length() == 0 || password.length() == 0) {
            statusText.setText("Please enter username and password.");
            return;
        }

        CredentialStore credentialStore =
                new CredentialStore(this);

        credentialStore.saveCredentials(
                username,
                password
        );

        statusText.setText("Authenticating...");
        authenticateButton.setEnabled(false);

        new Thread(new Runnable() {
            @Override
            public void run() {

                final AuthService authService =
                        new AuthService(MainActivity.this);

                final boolean success =
                        authService.authenticate();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (success) {
                            statusText.setText(
                                    "✓ Internet access granted"
                            );
                        } else {
                            statusText.setText(
                                    "Authentication failed."
                            );
                        }

                        authenticateButton.setEnabled(true);
                    }
                });
            }
        }).start();
    }

    private String readResponse(HttpURLConnection connection)
            throws Exception {

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

        StringBuilder result = new StringBuilder();

        String line;

        while ((line = reader.readLine()) != null) {
            result.append(line).append("\n");
        }

        reader.close();

        return result.toString();
    }
}