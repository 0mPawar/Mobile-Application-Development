package com.example.loginform;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    TextView tvWelcome, tvUserDetails;
    Button   btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // ----------------------------------------------------------------
        // Step 1: Link XML components
        // ----------------------------------------------------------------
        tvWelcome     = findViewById(R.id.tvWelcome);
        tvUserDetails = findViewById(R.id.tvUserDetails);
        btnLogout     = findViewById(R.id.btnLogout);

        // ----------------------------------------------------------------
        // Step 2: Retrieve data passed via Intent from MainActivity
        //         getStringExtra() / getBooleanExtra() read bundled values
        // ----------------------------------------------------------------
        Intent intent    = getIntent();
        String username  = intent.getStringExtra("USERNAME");
        String gender    = intent.getStringExtra("GENDER");
        boolean remember = intent.getBooleanExtra("REMEMBER_ME", false);

        // ----------------------------------------------------------------
        // Step 3: Display the user's details on the dashboard
        // ----------------------------------------------------------------
        tvWelcome.setText("Welcome, " + username + "! 👋");

        String details =
                "Username    : " + username               + "\n" +
                        "Gender      : " + gender                 + "\n" +
                        "Remember Me : " + (remember ? "Yes ✅" : "No ❌");

        tvUserDetails.setText(details);

        // ----------------------------------------------------------------
        // Step 4: LOGOUT button — navigate back to login screen
        //         Intent navigates back to MainActivity
        // ----------------------------------------------------------------
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HomeActivity.this,
                        "Logged out successfully!", Toast.LENGTH_SHORT).show();

                // Navigate back to login screen
                Intent logoutIntent = new Intent(HomeActivity.this, MainActivity.class);

                // Clear activity stack so Back button doesn't return to dashboard
                logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(logoutIntent);
                finish();
            }
        });
    }
}