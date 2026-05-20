package com.example.loginactivities;

// WelcomeActivity.java — Activity 2 (Welcome / Dashboard Screen)
//
// This Activity is launched by MainActivity via an Intent after
// successful login. It receives the username and login timestamp
// passed as Intent extras, and displays them.
//
// Also implements the full Activity Lifecycle:
// onCreate() → onStart() → onResume() → onPause() → onStop() → onDestroy()
//

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WelcomeActivity extends AppCompatActivity {

    // ── UI Components ────────────────────────────────────────────────────
    TextView tvWelcomeMessage;
    TextView tvShowUsername;
    TextView tvLoginTime;
    TextView tvWelcomeLifecycle;
    Button   btnLogout;
    Button   btnBackToLogin;

    // ── Lifecycle log ────────────────────────────────────────────────────
    StringBuilder lifecycleLog = new StringBuilder();

    // ====================================================================
    //  ACTIVITY LIFECYCLE METHODS — Activity 2
    // ====================================================================

    /**
     * onCreate() — Called when WelcomeActivity is first created.
     *
     * Key tasks here:
     *   1. setContentView() — load activity_welcome.xml
     *   2. getIntent()      — retrieve data sent from MainActivity
     *   3. getStringExtra() — read bundled username and login time
     *   4. Populate the UI with the received data
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the welcome screen layout
        setContentView(R.layout.activity_welcome);

        appendLog("onCreate()  — WelcomeActivity created");

        // ----------------------------------------------------------------
        // Step 1: Link XML components
        // ----------------------------------------------------------------
        tvWelcomeMessage   = findViewById(R.id.tvWelcomeMessage);
        tvShowUsername     = findViewById(R.id.tvShowUsername);
        tvLoginTime        = findViewById(R.id.tvLoginTime);
        tvWelcomeLifecycle = findViewById(R.id.tvWelcomeLifecycle);
        btnLogout          = findViewById(R.id.btnLogout);
        btnBackToLogin     = findViewById(R.id.btnBackToLogin);

        // ----------------------------------------------------------------
        // Step 2: Retrieve data passed from MainActivity via Intent
        //
        // getIntent()                  — returns the Intent that started
        //                                this Activity
        // intent.getStringExtra(key)   — reads a String value by key
        // intent.getBooleanExtra(key, default) — reads a boolean value
        //
        // If no extra is found for a key, getStringExtra returns null;
        // we provide a fallback value using a ternary operator.
        // ----------------------------------------------------------------
        Intent intent    = getIntent();
        String username  = intent.getStringExtra("USERNAME");
        String loginTime = intent.getStringExtra("LOGIN_TIME");

        // Fallback values in case extras were not passed
        if (username  == null) username  = "User";
        if (loginTime == null) loginTime = "Unknown";

        appendLog("getIntent() — username = " + username);

        // ----------------------------------------------------------------
        // Step 3: Populate the UI with received data
        // ----------------------------------------------------------------
        tvWelcomeMessage.setText("Welcome, " + username + "! 👋");
        tvShowUsername.setText(username);
        tvLoginTime.setText(loginTime);

        updateLogDisplay();

        // ----------------------------------------------------------------
        // Step 4: LOGOUT button
        //
        // Clears the back stack and returns to MainActivity.
        // FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK ensures that
        // pressing Back after logout exits the app rather than returning
        // to the welcome screen.
        // ----------------------------------------------------------------
        final String finalUsername = username;
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(WelcomeActivity.this,
                        "Goodbye, " + finalUsername + "!", Toast.LENGTH_SHORT).show();

                appendLog("btnLogout — navigating back to MainActivity");
                updateLogDisplay();

                // Create Intent back to login screen
                Intent logoutIntent = new Intent(
                        WelcomeActivity.this, MainActivity.class);

                // Clear entire back stack — prevents returning to WelcomeActivity
                logoutIntent.setFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK |
                                Intent.FLAG_ACTIVITY_CLEAR_TASK);

                startActivity(logoutIntent);
                finish();
            }
        });

        // ----------------------------------------------------------------
        // Step 5: BACK TO LOGIN button — simply finish this Activity,
        //         which pops the back stack and reveals MainActivity.
        // ----------------------------------------------------------------
        btnBackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appendLog("btnBack — finish() called");
                updateLogDisplay();
                // finish() destroys this Activity and goes back
                // to the previous one (MainActivity) in the stack
                finish();
            }
        });
    }

    /**
     * onStart() — WelcomeActivity becomes visible to the user.
     */
    @Override
    protected void onStart() {
        super.onStart();
        appendLog("onStart()   — WelcomeActivity visible");
        updateLogDisplay();
    }

    /**
     * onResume() — WelcomeActivity is in the foreground and interactive.
     */
    @Override
    protected void onResume() {
        super.onResume();
        appendLog("onResume()  — WelcomeActivity interactive");
        updateLogDisplay();
    }

    /**
     * onPause() — WelcomeActivity partially hidden (e.g. dialog opens).
     */
    @Override
    protected void onPause() {
        super.onPause();
        appendLog("onPause()   — WelcomeActivity partially hidden");
        updateLogDisplay();
    }

    /**
     * onStop() — WelcomeActivity no longer visible (user pressed Back).
     */
    @Override
    protected void onStop() {
        super.onStop();
        appendLog("onStop()    — WelcomeActivity not visible");
        updateLogDisplay();
    }

    /**
     * onDestroy() — WelcomeActivity being destroyed.
     * Called when finish() is invoked or system reclaims memory.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        appendLog("onDestroy() — WelcomeActivity destroyed");
    }

    // ====================================================================
    // HELPER METHODS
    // ====================================================================

    private void appendLog(String message) {
        String time = new SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                .format(new Date());
        lifecycleLog.append("[").append(time).append("]  ")
                .append(message).append("\n");
    }

    private void updateLogDisplay() {
        if (tvWelcomeLifecycle != null) {
            tvWelcomeLifecycle.setText(lifecycleLog.toString());
        }
    }
}