package com.example.progressbar;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // ── Section 1: Horizontal Determinate ProgressBar ───────────────────
    ProgressBar progressBarHorizontal;
    TextView    tvProgressPercent;
    Button      btnStart, btnReset;

    // ── Section 2: Circular Indeterminate ProgressBar ───────────────────
    ProgressBar progressBarCircular;
    TextView    tvCircularStatus;
    Button      btnShowCircular, btnHideCircular;

    // ── Section 3: Manual Step ProgressBar ──────────────────────────────
    ProgressBar progressBarManual;
    TextView    tvManualPercent;
    Button      btnIncrement, btnDecrement;

    // ── Status output ────────────────────────────────────────────────────
    TextView tvStatusOutput;

    // ── Handler for background thread simulation ─────────────────────────
    // Handler posts Runnable tasks back to the MAIN (UI) thread safely
    Handler  handler  = new Handler();
    int      currentProgress = 0;   // tracks horizontal bar progress
    boolean  isRunning = false;     // prevents multiple threads at once

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ----------------------------------------------------------------
        // Step 1: Link XML components to Java using findViewById()
        // ----------------------------------------------------------------
        progressBarHorizontal = findViewById(R.id.progressBarHorizontal);
        tvProgressPercent     = findViewById(R.id.tvProgressPercent);
        btnStart              = findViewById(R.id.btnStart);
        btnReset              = findViewById(R.id.btnReset);

        progressBarCircular   = findViewById(R.id.progressBarCircular);
        tvCircularStatus      = findViewById(R.id.tvCircularStatus);
        btnShowCircular       = findViewById(R.id.btnShowCircular);
        btnHideCircular       = findViewById(R.id.btnHideCircular);

        progressBarManual     = findViewById(R.id.progressBarManual);
        tvManualPercent       = findViewById(R.id.tvManualPercent);
        btnIncrement          = findViewById(R.id.btnIncrement);
        btnDecrement          = findViewById(R.id.btnDecrement);

        tvStatusOutput        = findViewById(R.id.tvStatusOutput);

        // ----------------------------------------------------------------
        // Step 2: START button — simulates auto-filling progress bar
        //         Uses a background Thread + Handler to update UI safely
        //         Direct UI updates from a background thread will CRASH —
        //         Handler.post() ensures update runs on the UI thread.
        // ----------------------------------------------------------------
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRunning) {
                    Toast.makeText(MainActivity.this,
                            "Progress already running!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Reset before starting
                currentProgress = 0;
                progressBarHorizontal.setProgress(0);
                isRunning = true;
                tvStatusOutput.setText("Status: Loading in progress...");

                // Background Thread — simulates a long-running task
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (currentProgress <= 95) {

                            // Post UI update to the main thread via Handler
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    // setProgress() updates the filled width of the bar
                                    progressBarHorizontal.setProgress(currentProgress);
                                    tvProgressPercent.setText("Progress: " + currentProgress + "%");

                                    // When complete, update status
                                    if (currentProgress == 100) {
                                        tvStatusOutput.setText("Status: Task Complete! ✅");
                                        isRunning = false;
                                        Toast.makeText(MainActivity.this,
                                                "Progress Complete!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                            currentProgress += 5;  // increment by 5% each step

                            try {
                                // Thread.sleep() pauses the background thread
                                // This simulates real processing delay (150ms per step)
                                Thread.sleep(150);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();  // starts the background thread
            }
        });

        // ----------------------------------------------------------------
        // Step 3: RESET button — stops progress and resets bar to 0
        // ----------------------------------------------------------------
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRunning = false;
                currentProgress = 0;
                progressBarHorizontal.setProgress(0);
                tvProgressPercent.setText("Progress: 0%");
                tvStatusOutput.setText("Status: Reset. Press START to begin.");
            }
        });

        // ----------------------------------------------------------------
        // Step 4: SHOW circular spinner — sets visibility to VISIBLE
        //         Indeterminate = true means it spins without an end value
        //         Used when the task duration is unknown (e.g., API call)
        // ----------------------------------------------------------------
        btnShowCircular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBarCircular.setVisibility(View.VISIBLE);
                tvCircularStatus.setText("Status: Loading... Please wait");
                tvStatusOutput.setText("Status: Circular spinner is active.");
            }
        });

        // ----------------------------------------------------------------
        // Step 5: HIDE circular spinner — sets visibility to GONE
        // ----------------------------------------------------------------
        btnHideCircular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBarCircular.setVisibility(View.GONE);
                tvCircularStatus.setText("Status: Idle");
                tvStatusOutput.setText("Status: Circular spinner hidden.");
            }
        });

        // ----------------------------------------------------------------
        // Step 6: INCREMENT button — manually increases progress by 10
        //         getProgress() reads current value; setProgress() sets it
        // ----------------------------------------------------------------
        btnIncrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current = progressBarManual.getProgress();
                if (current < 100) {
                    int updated = Math.min(current + 10, 100);  // cap at 100
                    progressBarManual.setProgress(updated);
                    tvManualPercent.setText("Manual Progress: " + updated + " / 100");
                    tvStatusOutput.setText("Status: Manual progress increased to " + updated + "%");
                    if (updated == 100) {
                        Toast.makeText(MainActivity.this,
                                "Manual Progress Complete!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this,
                            "Already at 100%!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // ----------------------------------------------------------------
        // Step 7: DECREMENT button — manually decreases progress by 10
        // ----------------------------------------------------------------
        btnDecrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current = progressBarManual.getProgress();
                if (current > 0) {
                    int updated = Math.max(current - 10, 0);  // floor at 0
                    progressBarManual.setProgress(updated);
                    tvManualPercent.setText("Manual Progress: " + updated + " / 100");
                    tvStatusOutput.setText("Status: Manual progress decreased to " + updated + "%");
                } else {
                    Toast.makeText(MainActivity.this,
                            "Already at 0%!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // ----------------------------------------------------------------
    // Step 8: Stop the background thread safely when Activity is destroyed
    //         Prevents memory leaks from lingering threads
    // ----------------------------------------------------------------
    @Override
    protected void onDestroy() {
        super.onDestroy();
        isRunning = false;
        handler.removeCallbacksAndMessages(null);
    }
}