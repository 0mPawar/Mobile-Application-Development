package com.example.loginactivities;
// MainActivity.java — Activity 1 (Login Screen)
//
// This is the LAUNCHER activity — the first screen the user sees.
// It contains the full Activity Lifecycle implementation as required
// by the practical: onCreate(), onStart(), onResume(), onPause(),
// onStop(), onDestroy().
//
// On successful login it uses an Intent to navigate to WelcomeActivity.
//

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // ── UI Components ────────────────────────────────────────────────────
    EditText    etUsername, etPassword;
    Button      btnLogin;
    ProgressBar progressBarLogin;
    TextView    tvLoginStatus;
    TextView    tvLifecycleLog;

    // ── Hardcoded demo credentials ────────────────────────────────────────
    final String VALID_USERNAME = "Admin";
    final String VALID_PASSWORD = "Admin@123";

    // ── Handler for login delay simulation ───────────────────────────────
    Handler handler = new Handler();

    // ── Lifecycle log string — appended in each lifecycle method ──────────
    StringBuilder lifecycleLog = new StringBuilder();

    // ====================================================================
    //  ACTIVITY LIFECYCLE METHODS
    //  Each method is called by Android at specific points in the
    //  Activity's life. Override them to perform appropriate tasks.
    // ====================================================================

    /**
     * onCreate() — Called when the Activity is FIRST CREATED.
     *
     * This is where you:
     *   • Call setContentView() to load the XML layout
     *   • Link XML views to Java variables using findViewById()
     *   • Set up event listeners
     *   • Initialize data / state
     *
     * Bundle savedInstanceState holds data saved from a previous
     * instance (e.g. after screen rotation).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // setContentView() inflates res/layout/activity_main.xml
        // and sets it as the UI for this Activity.
        setContentView(R.layout.activity_main);

        // Log this lifecycle event
        appendLog("onCreate()  — Activity created, UI loaded");

        // ----------------------------------------------------------------
        // Step 1: Link XML components to Java using findViewById()
        // ----------------------------------------------------------------
        etUsername       = findViewById(R.id.etUsername);
        etPassword       = findViewById(R.id.etPassword);
        btnLogin         = findViewById(R.id.btnLogin);
        progressBarLogin = findViewById(R.id.progressBarLogin);
        tvLoginStatus    = findViewById(R.id.tvLoginStatus);
        tvLifecycleLog   = findViewById(R.id.tvLifecycleLog);

        // ----------------------------------------------------------------
        // Step 2: LOGIN Button — validate credentials, navigate on success
        // ----------------------------------------------------------------
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                // ── Input Validation ─────────────────────────────────────
                if (username.isEmpty()) {
                    etUsername.setError("Username is required");
                    etUsername.requestFocus();
                    return;
                }
                if (password.isEmpty()) {
                    etPassword.setError("Password is required");
                    etPassword.requestFocus();
                    return;
                }

                // Show spinner, disable button
                progressBarLogin.setVisibility(View.VISIBLE);
                btnLogin.setEnabled(false);
                tvLoginStatus.setText("Authenticating...");
                tvLoginStatus.setTextColor(0xFF9FA8DA);

                // Simulate a 1.5-second authentication delay
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressBarLogin.setVisibility(View.GONE);
                        btnLogin.setEnabled(true);

                        if (username.equals(VALID_USERNAME)
                                && password.equals(VALID_PASSWORD)) {

                            // ── SUCCESS — navigate to WelcomeActivity ──────
                            tvLoginStatus.setText("✅ Login successful!");
                            tvLoginStatus.setTextColor(0xFF4CAF50);

                            appendLog("btnLogin clicked — credentials valid");
                            appendLog("startActivity(WelcomeActivity) called");

                            // Get current login timestamp
                            String timestamp = new SimpleDateFormat(
                                    "dd MMM yyyy  HH:mm:ss",
                                    Locale.getDefault()).format(new Date());

                            // ── Intent: pass data to WelcomeActivity ──────
                            //
                            // Intent(sourceActivity, targetActivity.class)
                            //   creates an explicit Intent for navigation.
                            //
                            // putExtra(key, value) bundles extra data to send.
                            // WelcomeActivity reads it with getIntent().getStringExtra(key)
                            //
                            Intent intent = new Intent(
                                    MainActivity.this, WelcomeActivity.class);
                            intent.putExtra("USERNAME",    username);
                            intent.putExtra("LOGIN_TIME",  timestamp);

                            startActivity(intent);
                            // Note: we do NOT call finish() here so the user
                            // can press Back to return to the login screen.

                        } else {
                            // ── FAILURE — show error ───────────────────────
                            tvLoginStatus.setText("❌ Invalid username or password!");
                            tvLoginStatus.setTextColor(0xFFEF9A9A);
//                            Toast.makeText(MainActivity.this,
//                                    "Login failed. Try admin / admin123",
//                                    Toast.LENGTH_SHORT).show();
                            appendLog("btnLogin clicked — invalid credentials");
                        }
                    }
                }, 1500);
            }
        });

        // Update log display after onCreate completes
        updateLogDisplay();
    }

    /**
     * onStart() — Called when the Activity becomes VISIBLE to the user.
     * Called after onCreate() or after onRestart() (when returning to
     * this activity from another).
     */
    @Override
    protected void onStart() {
        super.onStart();
        appendLog("onStart()   — Activity now visible");
        updateLogDisplay();
    }

    /**
     * onResume() — Called when the Activity starts INTERACTING with user.
     * The Activity is now in the foreground and ready to receive input.
     * This is where you should start animations, open exclusive resources,
     * or resume paused operations.
     */
    @Override
    protected void onResume() {
        super.onResume();
        appendLog("onResume()  — Activity in foreground (interactive)");
        updateLogDisplay();
    }

    /**
     * onPause() — Called when the Activity is PARTIALLY OBSCURED.
     * Another activity is coming to the foreground (e.g. a dialog appears,
     * user presses Home, or navigates to WelcomeActivity).
     * Release resources that aren't needed while paused.
     * Must execute quickly — do NOT do heavy work here.
     */
    @Override
    protected void onPause() {
        super.onPause();
        appendLog("onPause()   — Activity partially hidden");
        updateLogDisplay();
    }

    /**
     * onStop() — Called when the Activity is NO LONGER VISIBLE.
     * The Activity is completely hidden (e.g. another activity took over
     * the full screen, or the user pressed Home).
     * Release more resources here; save state if needed.
     */
    @Override
    protected void onStop() {
        super.onStop();
        appendLog("onStop()    — Activity not visible");
        updateLogDisplay();
    }

    /**
     * onRestart() — Called when a STOPPED Activity is about to start again.
     * Called before onStart() when the user navigates back to this Activity
     * (e.g. pressing Back from WelcomeActivity).
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        appendLog("onRestart() — Activity restarting (user came back)");
        updateLogDisplay();
    }

    /**
     * onDestroy() — Called before the Activity is DESTROYED.
     * Either the Activity is finishing (user pressed Back / finish() called)
     * or Android is destroying it to save memory.
     * Release ALL resources here to prevent memory leaks.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        // Cancel any pending Handler callbacks to prevent memory leaks
        handler.removeCallbacksAndMessages(null);
        appendLog("onDestroy() — Activity destroyed");
        // Note: updateLogDisplay() not called here — view may be gone
    }

    // ====================================================================
    // HELPER METHODS
    // ====================================================================

    /** Appends a timestamped message to the lifecycle log */
    private void appendLog(String message) {
        String time = new SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                .format(new Date());
        lifecycleLog.append("[").append(time).append("]  ").append(message).append("\n");
    }

    /** Updates the lifecycle log TextView with latest entries */
    private void updateLogDisplay() {
        if (tvLifecycleLog != null) {
            tvLifecycleLog.setText(lifecycleLog.toString());
        }
    }
}