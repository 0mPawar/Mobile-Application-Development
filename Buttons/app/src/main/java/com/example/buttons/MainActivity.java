package com.example.buttons;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // Declare UI component variables
    Button btnNormal;
    ImageButton imgBtn;
    ToggleButton toggleBtn;

    TextView tvBtnOutput;
    TextView tvImgBtnOutput;
    TextView tvToggleOutput;
    TextView tvClickCount;

    // Counter to track total button clicks
    int clickCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the XML layout for this Activity
        setContentView(R.layout.activity_main);

        // ----------------------------------------------------------------
        // Step 1: Link all XML components to Java using findViewById()
        // ----------------------------------------------------------------
        btnNormal = findViewById(R.id.btnNormal);
        imgBtn = findViewById(R.id.imgBtn);
        toggleBtn = findViewById(R.id.toggleBtn);
        tvBtnOutput = findViewById(R.id.tvBtnOutput);
        tvImgBtnOutput = findViewById(R.id.tvImgBtnOutput);
        tvToggleOutput = findViewById(R.id.tvToggleOutput);
        tvClickCount = findViewById(R.id.tvClickCount);

        // ----------------------------------------------------------------
        // Step 2: Event Handling for Normal Button
        //         setOnClickListener() fires each time the button is tapped
        // ----------------------------------------------------------------
        btnNormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCount++;
                tvBtnOutput.setText("Normal Button was clicked!");
                updateClickCount();
                Toast.makeText(MainActivity.this,
                        "Button Clicked!", Toast.LENGTH_SHORT).show();
            }
        });

        // ----------------------------------------------------------------
        // Step 3: Event Handling for ImageButton
        //         ImageButton works exactly like Button but shows an image
        // ----------------------------------------------------------------
        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCount++;
                tvImgBtnOutput.setText("▶ Play ImageButton was pressed!");
                updateClickCount();
                Toast.makeText(MainActivity.this,
                        "ImageButton Clicked!", Toast.LENGTH_SHORT).show();
            }
        });

        // ----------------------------------------------------------------
        // Step 4: Event Handling for ToggleButton
        //         setOnCheckedChangeListener() fires when state changes
        //         isChecked = true  → ON state
        //         isChecked = false → OFF state
        // ----------------------------------------------------------------
        toggleBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                clickCount++;
                if (isChecked) {
                    // Toggle is ON
                    tvToggleOutput.setText("Toggle is currently: ON  ✅");
                    tvToggleOutput.setTextColor(0xFF388E3C);   // Green color
                } else {
                    // Toggle is OFF
                    tvToggleOutput.setText("Toggle is currently: OFF ❌");
                    tvToggleOutput.setTextColor(0xFFD32F2F);   // Red color
                }
                updateClickCount();

                String state = isChecked ? "ON" : "OFF";
                Toast.makeText(MainActivity.this,
                        "Toggle switched to: " + state, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ----------------------------------------------------------------
    // Helper method: updates the total click counter TextView
    // ----------------------------------------------------------------
    private void updateClickCount() {
        tvClickCount.setText("Total Button Clicks: " + clickCount);
    }
}