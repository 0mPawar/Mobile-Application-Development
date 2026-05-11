package com.example.loginform;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // ── UI Components ────────────────────────────────────────────────────
    EditText    etUsername, etPassword;
    RadioGroup  rgGender;
    CheckBox    cbRememberMe;
    Button      btnLogin;
    ProgressBar progressBarLogin;
    TextView    tvLoginStatus, tvRegisterLink;

    // ── Hardcoded credentials for demo (In real apps, use a database) ───
    final String VALID_USERNAME = "AIOS";
    final String VALID_PASSWORD = "556624";

    // ── Handler to simulate login processing delay ───────────────────────
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ----------------------------------------------------------------
        // Step 1: Link XML components to Java using findViewById()
        // ----------------------------------------------------------------
        etUsername       = findViewById(R.id.etUsername);
        etPassword       = findViewById(R.id.etPassword);
        rgGender         = findViewById(R.id.rgGender);
        cbRememberMe     = findViewById(R.id.cbRememberMe);
        btnLogin         = findViewById(R.id.btnLogin);
        progressBarLogin = findViewById(R.id.progressBarLogin);
        tvLoginStatus    = findViewById(R.id.tvLoginStatus);
        tvRegisterLink   = findViewById(R.id.tvRegisterLink);

        // ----------------------------------------------------------------
        // Step 2: LOGIN Button click — validate input then authenticate
        // ----------------------------------------------------------------
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Read user inputs
                String username = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                // ── Input Validation ────────────────────────────────────

                // Check username is not empty
                if (username.isEmpty()) {
                    etUsername.setError("Username is required");
                    etUsername.requestFocus();
                    return;
                }

                // Check password is not empty
                if (password.isEmpty()) {
                    etPassword.setError("Password is required");
                    etPassword.requestFocus();
                    return;
                }

                // Check minimum password length
                if (password.length() < 4) {
                    etPassword.setError("Password must be at least 4 characters");
                    etPassword.requestFocus();
                    return;
                }

                // Check gender is selected
                if (rgGender.getCheckedRadioButtonId() == -1) {
                    tvLoginStatus.setText("⚠ Please select your gender.");
                    tvLoginStatus.setTextColor(0xFFFF6F00);
                    Toast.makeText(MainActivity.this,
                            "Please select your gender!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // ── Read Gender selection ────────────────────────────────
                int selectedId = rgGender.getCheckedRadioButtonId();
                RadioButton selectedGender = findViewById(selectedId);
                String gender = selectedGender.getText().toString();

                // ── Read Remember Me state ───────────────────────────────
                boolean rememberMe = cbRememberMe.isChecked();

                // ── Show ProgressBar, disable button to prevent re-click ─
                progressBarLogin.setVisibility(View.VISIBLE);
                btnLogin.setEnabled(false);
                tvLoginStatus.setText("Authenticating...");
                tvLoginStatus.setTextColor(0xFF3F51B5);

                // ── Simulate a 2-second network/auth delay using Handler ─
                // In a real app, this would be an API call on a background thread
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Hide progress bar and re-enable button
                        progressBarLogin.setVisibility(View.GONE);
                        btnLogin.setEnabled(true);

                        // ── Credential Check ─────────────────────────────
                        if (username.equals(VALID_USERNAME) && password.equals(VALID_PASSWORD)) {

                            // SUCCESS — navigate to HomeActivity via Intent
                            Toast.makeText(MainActivity.this,
                                    "Login Successful! Welcome, " + username,
                                    Toast.LENGTH_SHORT).show();

                            // Bundle extra data to pass to HomeActivity
                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            intent.putExtra("USERNAME",    username);
                            intent.putExtra("GENDER",      gender);
                            intent.putExtra("REMEMBER_ME", rememberMe);
                            startActivity(intent);

                            // Optionally finish login screen so Back doesn't return to it
                            finish();

                        } else {
                            // FAILURE — show error message
                            tvLoginStatus.setText("❌ Invalid username or password!");
                            tvLoginStatus.setTextColor(0xFFD32F2F);
                            Toast.makeText(MainActivity.this,
                                    "Login Failed! Check your credentials.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }, 2000); // 2000ms = 2 second delay
            }
        });

        // ----------------------------------------------------------------
        // Step 3: Register link click — show a message (placeholder)
        // ----------------------------------------------------------------
        tvRegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,
                        "Register screen coming soon!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ── Clean up handler on destroy to prevent memory leaks ─────────────
    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}