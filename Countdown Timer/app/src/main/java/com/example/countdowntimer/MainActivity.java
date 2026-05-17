package com.example.countdowntimer;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.DrawableCompat;

public class MainActivity extends AppCompatActivity {

    // ── Input fields ──────────────────────────────────────────────────────
    EditText etHours, etMinutes, etSeconds;

    // ── Display views ────────────────────────────────────────────────────
    TextView     tvCountdown, tvStatus, tvMillis, tvFinishMessage;
    ProgressBar  progressTimer;

    // ── Control buttons ──────────────────────────────────────────────────
    Button btnStartPause, btnReset, btnLap;
    Button btn30s, btn1m, btn5m, btn10m;

    // ── Lap log container ────────────────────────────────────────────────
    LinearLayout llLapContainer;

    // ====================================================================
    // CountDownTimer STATE VARIABLES
    // ====================================================================

    CountDownTimer countDownTimer;  // Created fresh on each START
    long totalTimeMs  = 0;          // Total time set by the user (ms)
    long timeLeftMs   = 0;          // Time remaining when PAUSE pressed

    // Tracks current app state
    enum TimerState { IDLE, RUNNING, PAUSED, FINISHED }
    TimerState timerState = TimerState.IDLE;

    int lapCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ----------------------------------------------------------------
        // Step 1: Link all XML components to Java using findViewById()
        // ----------------------------------------------------------------
        etHours   = findViewById(R.id.etHours);
        etMinutes = findViewById(R.id.etMinutes);
        etSeconds = findViewById(R.id.etSeconds);

        tvCountdown     = findViewById(R.id.tvCountdown);
        tvStatus        = findViewById(R.id.tvStatus);
        tvMillis        = findViewById(R.id.tvMillis);
        tvFinishMessage = findViewById(R.id.tvFinishMessage);
        progressTimer   = findViewById(R.id.progressTimer);

        btnStartPause = findViewById(R.id.btnStartPause);
        btnReset      = findViewById(R.id.btnReset);
        btnLap        = findViewById(R.id.btnLap);
        btn30s        = findViewById(R.id.btn30s);
        btn1m         = findViewById(R.id.btn1m);
        btn5m         = findViewById(R.id.btn5m);
        btn10m        = findViewById(R.id.btn10m);

        llLapContainer = findViewById(R.id.llLapContainer);

        // ----------------------------------------------------------------
        // Step 2: PRESET buttons — fill HH:MM:SS fields automatically
        // ----------------------------------------------------------------
        btn30s.setOnClickListener(v -> applyPreset(0, 0, 30));
        btn1m.setOnClickListener(v  -> applyPreset(0, 1, 0));
        btn5m.setOnClickListener(v  -> applyPreset(0, 5, 0));
        btn10m.setOnClickListener(v -> applyPreset(0, 10, 0));

        // ----------------------------------------------------------------
        // Step 3: START / PAUSE / RESUME button
        //         IDLE/FINISHED → START | RUNNING → PAUSE | PAUSED → RESUME
        // ----------------------------------------------------------------
        btnStartPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (timerState) {
                    case IDLE:
                    case FINISHED: startTimer();  break;
                    case RUNNING:  pauseTimer();  break;
                    case PAUSED:   resumeTimer(); break;
                }
            }
        });

        // ----------------------------------------------------------------
        // Step 4: RESET button
        // ----------------------------------------------------------------
        btnReset.setOnClickListener(v -> resetTimer());

        // ----------------------------------------------------------------
        // Step 5: LAP button
        // ----------------------------------------------------------------
        btnLap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timerState == TimerState.RUNNING) {
                    recordLap();
                } else {
                    Toast.makeText(MainActivity.this,
                            "Timer must be running to record a lap",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // ====================================================================
    // startTimer() — reads user input, validates, creates CountDownTimer
    // ====================================================================
    private void startTimer() {
        int h = parseField(etHours);
        int m = parseField(etMinutes);
        int s = parseField(etSeconds);

        if (h == 0 && m == 0 && s == 0) {
            Toast.makeText(this, "Please set a time greater than 0",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (m >= 60 || s >= 60) {
            Toast.makeText(this, "Minutes and seconds must be 0–59",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        totalTimeMs = ((h * 3600L) + (m * 60L) + s) * 1000L;
        timeLeftMs  = totalTimeMs;

        lapCount = 0;
        llLapContainer.removeAllViews();
        llLapContainer.setVisibility(View.GONE);
        tvFinishMessage.setVisibility(View.GONE);
        tvCountdown.setTextColor(Color.WHITE);
        setProgressBarColor(Color.parseColor("#3F51B5")); // reset to blue

        buildAndStartTimer(timeLeftMs);
    }

    // ====================================================================
    // buildAndStartTimer()
    //
    // CountDownTimer(millisInFuture, countDownInterval)
    //   onTick()   — fires every 50ms; update HH:MM:SS display here
    //   onFinish() — fires once when countdown hits zero
    //
    // Both callbacks run on the MAIN (UI) thread — no Handler needed.
    // ====================================================================
    private void buildAndStartTimer(long durationMs) {
        countDownTimer = new CountDownTimer(durationMs, 50) {

            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftMs = millisUntilFinished;

                long totalSec = millisUntilFinished / 1000;
                long h  = totalSec / 3600;
                long m  = (totalSec % 3600) / 60;
                long s  = totalSec % 60;
                long ms = millisUntilFinished % 1000;

                tvCountdown.setText(String.format("%02d:%02d:%02d", h, m, s));
                tvMillis.setText(String.format(".%03d", ms));

                // Update progress bar: (remaining / total) × 100
                int progress = (int) ((millisUntilFinished * 100) / totalTimeMs);
                progressTimer.setProgress(progress);

                // Color warning — turn orange at 30s, red at 10s
                // FIX: Use setProgressBarColor() helper instead of
                //      ProgressBar.setProgressTint(ColorStateList) which
                //      is not reliably available on all API levels.
                if (millisUntilFinished <= 10_000) {
                    tvCountdown.setTextColor(Color.parseColor("#FF5252"));
                    setProgressBarColor(Color.parseColor("#FF5252"));
                } else if (millisUntilFinished <= 30_000) {
                    tvCountdown.setTextColor(Color.parseColor("#F5A623"));
                    setProgressBarColor(Color.parseColor("#F5A623"));
                }
            }

            @Override
            public void onFinish() {
                tvCountdown.setText("00:00:00");
                tvMillis.setText(".000");
                progressTimer.setProgress(0);

                tvFinishMessage.setVisibility(View.VISIBLE);
                tvStatus.setText("FINISHED ✅");
                tvStatus.setTextColor(Color.parseColor("#4CAF50"));
                tvCountdown.setTextColor(Color.parseColor("#4CAF50"));

                btnStartPause.setText("▶  START");
                btnStartPause.setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(
                                Color.parseColor("#3F51B5")));

                timerState = TimerState.FINISHED;
                Toast.makeText(MainActivity.this,
                        "🎉 Time's Up!", Toast.LENGTH_LONG).show();
            }

        }.start();

        timerState = TimerState.RUNNING;
        tvStatus.setText("RUNNING ▶");
        tvStatus.setTextColor(Color.parseColor("#4CAF50"));
        btnStartPause.setText("⏸  PAUSE");
        btnStartPause.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(
                        Color.parseColor("#F57F17")));
    }

    // ====================================================================
    // pauseTimer() — cancel() + save timeLeftMs
    // CountDownTimer has no native pause; we cancel and store remaining ms
    // ====================================================================
    private void pauseTimer() {
        if (countDownTimer != null) countDownTimer.cancel();
        timerState = TimerState.PAUSED;
        tvStatus.setText("PAUSED ⏸");
        tvStatus.setTextColor(Color.parseColor("#F5A623"));
        btnStartPause.setText("▶  RESUME");
        btnStartPause.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(
                        Color.parseColor("#4CAF50")));
    }

    // ====================================================================
    // resumeTimer() — re-create CountDownTimer from saved timeLeftMs
    // ====================================================================
    private void resumeTimer() {
        buildAndStartTimer(timeLeftMs);
    }

    // ====================================================================
    // resetTimer() — cancel timer and reset all UI + state to defaults
    // ====================================================================
    private void resetTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }

        timerState  = TimerState.IDLE;
        totalTimeMs = 0;
        timeLeftMs  = 0;
        lapCount    = 0;

        tvCountdown.setText("00:00:00");
        tvMillis.setText(".000");
        tvStatus.setText("READY");
        tvStatus.setTextColor(Color.parseColor("#7986CB"));
        tvCountdown.setTextColor(Color.WHITE);
        progressTimer.setProgress(100);

        // FIX: DrawableCompat.setTint() instead of setProgressTint()
        setProgressBarColor(Color.parseColor("#3F51B5"));

        tvFinishMessage.setVisibility(View.GONE);
        btnStartPause.setText("▶  START");
        btnStartPause.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(
                        Color.parseColor("#3F51B5")));

        etHours.setText("");
        etMinutes.setText("");
        etSeconds.setText("");

        llLapContainer.removeAllViews();
        llLapContainer.setVisibility(View.GONE);
    }

    // ====================================================================
    // recordLap() — snapshot current time and add to lap log
    // ====================================================================
    private void recordLap() {
        lapCount++;
        long totalSec = timeLeftMs / 1000;
        long h = totalSec / 3600;
        long m = (totalSec % 3600) / 60;
        long s = totalSec % 60;
        String lapTime = String.format("%02d:%02d:%02d", h, m, s);

        TextView lapEntry = new TextView(this);
        lapEntry.setText("  🏁 Lap " + lapCount + "  —  " + lapTime);
        lapEntry.setTextSize(14f);
        lapEntry.setTextColor(Color.parseColor("#9FA8DA"));
        lapEntry.setPadding(8, 10, 8, 10);
        if (lapCount % 2 == 0) {
            lapEntry.setBackgroundColor(Color.parseColor("#1E2A4A"));
        }

        llLapContainer.addView(lapEntry, 0);
        llLapContainer.setVisibility(View.VISIBLE);
        Toast.makeText(this, "Lap " + lapCount + " — " + lapTime,
                Toast.LENGTH_SHORT).show();
    }

    // ====================================================================
    // setProgressBarColor() — compatibility helper
    //
    // WHY THIS IS NEEDED:
    //   ProgressBar.setProgressTint(ColorStateList) compiles but may throw
    //   "cannot find symbol" depending on the project's compileSdkVersion
    //   and minSdkVersion settings. The correct cross-version approach is:
    //     1. getProgressDrawable()          — get the current drawable
    //     2. DrawableCompat.wrap().mutate() — wrap for compat + clone it
    //     3. DrawableCompat.setTint()       — apply tint color
    //     4. setProgressDrawable()          — put the tinted drawable back
    //   DrawableCompat is part of AndroidX core — no extra dependency needed.
    // ====================================================================
    private void setProgressBarColor(int colorInt) {
        Drawable drawable = progressTimer.getProgressDrawable();
        if (drawable != null) {
            Drawable wrapped = DrawableCompat.wrap(drawable.mutate());
            DrawableCompat.setTint(wrapped, colorInt);
            progressTimer.setProgressDrawable(wrapped);
        }
    }

    // ====================================================================
    // applyPreset() — fills HH:MM:SS input fields with given values
    // ====================================================================
    private void applyPreset(int h, int m, int s) {
        etHours.setText(String.valueOf(h));
        etMinutes.setText(String.valueOf(m));
        etSeconds.setText(String.valueOf(s));
    }

    // ====================================================================
    // parseField() — safely converts EditText content to int
    // ====================================================================
    private int parseField(EditText et) {
        String val = et.getText().toString().trim();
        if (val.isEmpty()) return 0;
        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    // ====================================================================
    // onDestroy() — cancel timer to prevent memory leaks
    // ====================================================================
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) countDownTimer.cancel();
    }

    // ====================================================================
    // onPause() — auto-pause timer if user navigates away
    // ====================================================================
    @Override
    protected void onPause() {
        super.onPause();
        if (timerState == TimerState.RUNNING) pauseTimer();
    }
}