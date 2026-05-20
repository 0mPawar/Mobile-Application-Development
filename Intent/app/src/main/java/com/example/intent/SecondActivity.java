package com.example.intent;

// SecondActivity.java
// Launched via EXPLICIT Intent from MainActivity.
// Reads the Name, Email and Message passed via Intent.putExtra().
// Can send a reply back to MainActivity using setResult().
//

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SecondActivity extends AppCompatActivity {

    // ── UI components ─────────────────────────────────────────────────────
    TextView tvReceivedName, tvReceivedEmail, tvReceivedMessage;
    Button   btnSendReply, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        // ----------------------------------------------------------------
        // Step 1: Link XML components to Java
        // ----------------------------------------------------------------
        tvReceivedName    = findViewById(R.id.tvReceivedName);
        tvReceivedEmail   = findViewById(R.id.tvReceivedEmail);
        tvReceivedMessage = findViewById(R.id.tvReceivedMessage);
        btnSendReply      = findViewById(R.id.btnSendReply);
        btnBack           = findViewById(R.id.btnBack);

        // ----------------------------------------------------------------
        // Step 2: Read data sent by MainActivity via Intent.putExtra()
        //
        // getIntent()            — returns the Intent that launched this Activity
        // getStringExtra(key)    — retrieves the String value for the given key
        //                          Returns null if key not found.
        // ----------------------------------------------------------------
        Intent intent = getIntent();

        String name    = intent.getStringExtra("NAME");
        String email   = intent.getStringExtra("EMAIL");
        String message = intent.getStringExtra("MESSAGE");

        // Fallback in case extras are missing
        if (name    == null) name    = "Not provided";
        if (email   == null) email   = "Not provided";
        if (message == null) message = "No message";

        // ----------------------------------------------------------------
        // Step 3: Display the received data in the UI
        // ----------------------------------------------------------------
        tvReceivedName.setText(name);
        tvReceivedEmail.setText(email);
        tvReceivedMessage.setText(message);

        // ----------------------------------------------------------------
        // Step 4: SEND REPLY button
        //
        // setResult(resultCode, replyIntent) — sends data BACK to MainActivity.
        // MainActivity receives it in onActivityResult().
        //
        // RESULT_OK    — indicates the action was completed successfully
        // RESULT_CANCELED — indicates the user cancelled
        // ----------------------------------------------------------------
        final String finalName = name;
        btnSendReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a reply Intent with data to send back
                Intent replyIntent = new Intent();
                replyIntent.putExtra("REPLY",
                        "Hello " + finalName + "! Message received. 👍");

                // setResult() stores the result; it is delivered when finish() is called
                setResult(RESULT_OK, replyIntent);

                Toast.makeText(SecondActivity.this,
                        "Reply sent to MainActivity!", Toast.LENGTH_SHORT).show();

                // finish() closes this Activity and returns to MainActivity
                finish();
            }
        });

        // ----------------------------------------------------------------
        // Step 5: BACK button — simply finish() to go back
        // ----------------------------------------------------------------
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Return without a result
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }
}