package com.example.splashscreen;

// MainActivity.java — Main Home Screen
// This activity is launched by SplashActivity after the 3-second delay.
// It represents the actual app content the user interacts with.

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // ----------------------------------------------------------------
    // Activity Lifecycle — onCreate()
    //
    // onCreate() is called when Android creates this Activity.
    // It is the entry point for Activity initialization:
    //   1. setContentView() — inflates and sets the XML layout
    //   2. findViewById()   — links XML views to Java variables
    //   3. Event listeners  — attach click/touch handlers
    //
    // After SplashActivity calls startActivity(intent) + finish(),
    // Android calls this Activity's onCreate() and displays it.
    // ----------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the main home screen layout
        setContentView(R.layout.activity_main);

        // In a real app, you would:
        // - Load user session data here
        // - Set up navigation drawer / bottom nav
        // - Initialize ViewModels, RecyclerViews, etc.
    }
}