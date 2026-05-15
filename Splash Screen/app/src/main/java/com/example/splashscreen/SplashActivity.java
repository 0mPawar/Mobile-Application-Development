package com.example.splashscreen;

// SplashActivity.java
// This is the LAUNCHER Activity — the very first screen the user sees.
// It displays the brand logo + name for 3 seconds, then launches MainActivity.

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.AnimationSet;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    // ── UI Components ────────────────────────────────────────────────────
    ImageView   ivLogo;
    TextView    tvAppName, tvTagline, tvLoading;
    ProgressBar progressBarSplash;

    // ── Handler for timed navigation ─────────────────────────────────────
    // Handler posts Runnables to the main thread after a delay.
    // This is the standard Android way to implement a timed splash screen.
    Handler handler = new Handler();

    // ── Splash duration (3 seconds = 3000 ms) ────────────────────────────
    static final int SPLASH_DURATION_MS = 3000;

    // ── Progress bar updater ──────────────────────────────────────────────
    int progressValue = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ----------------------------------------------------------------
        // Step 1: Set the splash screen layout
        // ----------------------------------------------------------------
        setContentView(R.layout.activity_splash);

        // ----------------------------------------------------------------
        // Step 2: Link components
        // ----------------------------------------------------------------
        ivLogo           = findViewById(R.id.ivLogo);
        tvAppName        = findViewById(R.id.tvAppName);
        tvTagline        = findViewById(R.id.tvTagline);
        tvLoading        = findViewById(R.id.tvLoading);
        progressBarSplash= findViewById(R.id.progressBarSplash);

        // ----------------------------------------------------------------
        // Step 3: Apply entrance animations to the logo and text
        //
        // AlphaAnimation(fromAlpha, toAlpha) — fade in/out
        // ScaleAnimation(fromX, toX, fromY, toY, pivotType, pivotX, pivotY)
        //   — scales from small to full size (zoom-in effect)
        // AnimationSet — combines multiple animations together
        // ----------------------------------------------------------------
        animateLogo();
        animateText();

        // ----------------------------------------------------------------
        // Step 4: Animate the progress bar from 0 → 100 over 3 seconds
        //         Uses a Runnable posted every 30ms (100 steps × 30ms = 3s)
        // ----------------------------------------------------------------
        startProgressAnimation();

        // ----------------------------------------------------------------
        // Step 5: Animate "Loading..." dots using Handler
        // ----------------------------------------------------------------
        animateLoadingDots();

        // ----------------------------------------------------------------
        // Step 6: Navigate to MainActivity after SPLASH_DURATION_MS
        //
        //  handler.postDelayed(Runnable, delayMillis)
        //    → schedules the Runnable to run after the delay on UI thread
        //
        //  Intent(context, TargetActivity.class)
        //    → creates an intent to open MainActivity
        //
        //  startActivity(intent)
        //    → launches MainActivity
        //
        //  finish()
        //    → removes SplashActivity from the back stack so pressing
        //      Back in MainActivity doesn't return to the splash screen
        // ----------------------------------------------------------------
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Create Intent to navigate to the main app screen
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);

                // finish() is critical — removes this activity from back stack
                finish();
            }
        }, SPLASH_DURATION_MS);
    }

    // ====================================================================
    // animateLogo() — zoom-in + fade-in on the logo ImageView
    // ====================================================================
    private void animateLogo() {
        // ScaleAnimation: start at 30% size → 100% size
        // pivot = RELATIVE_TO_SELF, 0.5f means scale from the center
        ScaleAnimation scaleAnim = new ScaleAnimation(
                0.3f, 1.0f,                                    // X: 30% → 100%
                0.3f, 1.0f,                                    // Y: 30% → 100%
                Animation.RELATIVE_TO_SELF, 0.5f,             // pivot X = center
                Animation.RELATIVE_TO_SELF, 0.5f              // pivot Y = center
        );
        scaleAnim.setDuration(800);

        // AlphaAnimation: start fully transparent → fully opaque
        AlphaAnimation fadeAnim = new AlphaAnimation(0f, 1f);
        fadeAnim.setDuration(800);

        // AnimationSet: run both at the same time (setShareInterpolator=true)
        AnimationSet animSet = new AnimationSet(true);
        animSet.addAnimation(scaleAnim);
        animSet.addAnimation(fadeAnim);
        animSet.setFillAfter(true);  // keep final state after animation ends

        ivLogo.startAnimation(animSet);
    }

    // ====================================================================
    // animateText() — fade-in on app name and tagline with slight delay
    // ====================================================================
    private void animateText() {
        // App name fades in starting at 600ms
        AlphaAnimation nameAnim = new AlphaAnimation(0f, 1f);
        nameAnim.setDuration(700);
        nameAnim.setStartOffset(600);    // delay start by 600ms
        nameAnim.setFillAfter(true);
        tvAppName.startAnimation(nameAnim);

        // Tagline fades in starting at 1000ms
        AlphaAnimation tagAnim = new AlphaAnimation(0f, 1f);
        tagAnim.setDuration(700);
        tagAnim.setStartOffset(1000);
        tagAnim.setFillAfter(true);
        tvTagline.startAnimation(tagAnim);
    }

    // ====================================================================
    // startProgressAnimation() — fills progress bar from 0 to 100
    //   - 100 steps over 3000ms = 1 step every 30ms
    //   - handler.postDelayed() schedules each step
    // ====================================================================
    private void startProgressAnimation() {
        Runnable progressRunnable = new Runnable() {
            @Override
            public void run() {
                if (progressValue <= 100) {
                    progressBarSplash.setProgress(progressValue);
                    progressValue += 2;   // increment by 2 each step
                    handler.postDelayed(this, 60);  // 60ms × 50 steps = 3000ms
                }
            }
        };
        handler.post(progressRunnable);
    }

    // ====================================================================
    // animateLoadingDots() — cycles "Loading." → "Loading.." → "Loading..."
    //   Creates a simple text animation by cycling dot count every 400ms
    // ====================================================================
    private void animateLoadingDots() {
        final String[] dots = {"Loading.", "Loading..", "Loading..."};
        final int[]    index = {0};

        Runnable dotsRunnable = new Runnable() {
            @Override
            public void run() {
                if (tvLoading != null) {
                    tvLoading.setText(dots[index[0] % 3]);
                    index[0]++;
                    handler.postDelayed(this, 400);
                }
            }
        };
        handler.postDelayed(dotsRunnable, 1200);
    }

    // ====================================================================
    // onDestroy() — clean up the handler to prevent memory leaks
    //   If the user closes the app before splash finishes, the delayed
    //   Runnable would otherwise still fire on a destroyed activity.
    // ====================================================================
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove ALL pending Runnables posted by this handler
        handler.removeCallbacksAndMessages(null);
    }
}