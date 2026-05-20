package com.example.modeswitcherairplanesilentvibrateloudmode;

import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.content.Context;

public class MainActivity extends AppCompatActivity
        implements ModeReceiver.ModeChangeListener {

    // ── UI Components ─────────────────────────────────────────────────────
    TextView tvModeIcon, tvCurrentMode;
    TextView tvAirplaneStatus, tvRingerMode, tvVolume;
    TextView tvReceiverLog;
    Button   btnLoudMode, btnSilentMode, btnVibrateMode;
    Button   btnAirplaneSettings, btnClearLog, btnRefresh;

    // ── AudioManager — controls ringer and volume ─────────────────────────
    // AudioManager is a system service retrieved via getSystemService().
    // It provides:
    //   setRingerMode(int mode)    — switch between NORMAL/SILENT/VIBRATE
    //   getRingerMode()            — read current ringer state
    //   getStreamVolume(stream)    — read volume level for a stream type
    AudioManager audioManager;

    // ── BroadcastReceiver instance ────────────────────────────────────────
    ModeReceiver modeReceiver;

    // ── Log of broadcast events received ────────────────────────────────
    StringBuilder receiverLog = new StringBuilder();

    // ====================================================================
    // onCreate() — Activity setup
    // ====================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ----------------------------------------------------------------
        // Step 1: Link XML views to Java
        // ----------------------------------------------------------------
        tvModeIcon       = findViewById(R.id.tvModeIcon);
        tvCurrentMode    = findViewById(R.id.tvCurrentMode);
        tvAirplaneStatus = findViewById(R.id.tvAirplaneStatus);
        tvRingerMode     = findViewById(R.id.tvRingerMode);
        tvVolume         = findViewById(R.id.tvVolume);
        tvReceiverLog    = findViewById(R.id.tvReceiverLog);

        btnLoudMode         = findViewById(R.id.btnLoudMode);
        btnSilentMode       = findViewById(R.id.btnSilentMode);
        btnVibrateMode      = findViewById(R.id.btnVibrateMode);
        btnAirplaneSettings = findViewById(R.id.btnAirplaneSettings);
        btnClearLog         = findViewById(R.id.btnClearLog);
        btnRefresh          = findViewById(R.id.btnRefresh);

        // ----------------------------------------------------------------
        // Step 2: Get AudioManager system service
        // AudioManager manages audio routing, streams, and ringer mode.
        // ----------------------------------------------------------------
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        // ----------------------------------------------------------------
        // Step 3: Create BroadcastReceiver instance and set callback
        // ----------------------------------------------------------------
        modeReceiver = new ModeReceiver();
        ModeReceiver.setListener(this);   // 'this' implements ModeChangeListener

        // ----------------------------------------------------------------
        // Step 4: Build IntentFilter — specifies WHICH broadcasts to receive
        //
        // IntentFilter acts like a whitelist: the BroadcastReceiver only
        // receives broadcasts whose action matches an entry in the filter.
        // Multiple actions can be added to a single filter object.
        // ----------------------------------------------------------------
        IntentFilter filter = new IntentFilter();

        // System broadcasts
        filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);  // Airplane toggle
        filter.addAction(AudioManager.RINGER_MODE_CHANGED_ACTION);// Ringer change
        filter.addAction(Intent.ACTION_SCREEN_ON);              // Screen wakes up
        filter.addAction(Intent.ACTION_SCREEN_OFF);             // Screen sleeps
        filter.addAction(Intent.ACTION_POWER_CONNECTED);        // Charger plugged
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);     // Charger unplugged

        // Custom broadcast action defined by us
        filter.addAction("com.example.practical21.ACTION_CUSTOM_MODE");

        // ----------------------------------------------------------------
        // Step 5: Register BroadcastReceiver DYNAMICALLY
        //
        // DYNAMIC registration (in code) vs STATIC (in Manifest):
        //
        // Dynamic (registerReceiver):
        //   + Only active while Activity is running — more efficient
        //   + Works for broadcasts that can't be registered in Manifest
        //     (e.g. ACTION_SCREEN_ON, ACTION_SCREEN_OFF)
        //   - Stops receiving when Activity is destroyed
        //
        // Static (AndroidManifest.xml):
        //   + Receives broadcasts even when app is not running
        //   - Many implicit broadcasts no longer work this way (API 26+)
        //
        // For ACTION_AIRPLANE_MODE_CHANGED we use dynamic registration.
        // ----------------------------------------------------------------
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(
                    modeReceiver,
                    filter,
                    Context.RECEIVER_NOT_EXPORTED
            );
        } else {
            registerReceiver(modeReceiver, filter);
        }

        appendLog("BroadcastReceiver registered ✅");
        appendLog("Listening for: Airplane, Ringer, Screen, Power events");

        // ----------------------------------------------------------------
        // Step 6: Read initial status
        // ----------------------------------------------------------------
        refreshStatus();

        // ================================================================
        // MODE BUTTON HANDLERS
        // ================================================================

        // ── LOUD MODE ─────────────────────────────────────────────────────
        // AudioManager.RINGER_MODE_NORMAL = 2
        // Sets device to full ring with sound.
        btnLoudMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // setRingerMode() changes the device's ringer/notification mode
                audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                applyModeUI("LOUD");
                sendCustomBroadcast("Switched to LOUD MODE by user");
                appendLog("User tapped: LOUD MODE button");
                Toast.makeText(MainActivity.this,
                        "🔊 Loud Mode activated", Toast.LENGTH_SHORT).show();
            }
        });

        // ── SILENT MODE ───────────────────────────────────────────────────
        // AudioManager.RINGER_MODE_SILENT = 0
        // No ring, no vibration.
        // Note: On Android 5.0+ (API 21+), if Do Not Disturb is active,
        // setRingerMode(SILENT) may require a special permission.
        btnSilentMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (audioManager.isVolumeFixed()) {
                    Toast.makeText(MainActivity.this,
                            "Volume is fixed on this device",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    applyModeUI("SILENT");
                    sendCustomBroadcast("Switched to SILENT MODE by user");
                    appendLog("User tapped: SILENT MODE button");
                    Toast.makeText(MainActivity.this,
                            "🔇 Silent Mode activated", Toast.LENGTH_SHORT).show();
                } catch (SecurityException e) {
                    Toast.makeText(MainActivity.this,
                            "Permission needed: Allow Do Not Disturb access in Settings",
                            Toast.LENGTH_LONG).show();
                    // Open DND access settings
                    startActivity(new Intent(
                            Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS));
                }
            }
        });

        // ── VIBRATE MODE ──────────────────────────────────────────────────
        // AudioManager.RINGER_MODE_VIBRATE = 1
        // No sound, but device vibrates for calls/notifications.
        btnVibrateMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                    applyModeUI("VIBRATE");
                    sendCustomBroadcast("Switched to VIBRATE MODE by user");
                    appendLog("User tapped: VIBRATE MODE button");
                    Toast.makeText(MainActivity.this,
                            "📳 Vibrate Mode activated", Toast.LENGTH_SHORT).show();
                } catch (SecurityException e) {
                    Toast.makeText(MainActivity.this,
                            "Permission needed for vibrate mode",
                            Toast.LENGTH_LONG).show();
                    startActivity(new Intent(
                            Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS));
                }
            }
        });

        // ── AIRPLANE MODE SETTINGS ────────────────────────────────────────
        btnAirplaneSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open device Wireless Settings where user can toggle Airplane mode
                startActivity(new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS));
                appendLog("Opened Airplane Mode Settings");
            }
        });

        // ── CLEAR LOG ─────────────────────────────────────────────────────
        btnClearLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                receiverLog.setLength(0);
                tvReceiverLog.setText("Log cleared. Waiting for events...");
            }
        });

        // ── REFRESH ───────────────────────────────────────────────────────
        btnRefresh.setOnClickListener(v -> refreshStatus());
    }

    // ====================================================================
    // onModeChanged() — callback from ModeReceiver (ModeChangeListener)
    //
    // Called on the MAIN thread when the BroadcastReceiver's onReceive()
    // fires. Updates the log TextView and refreshes status.
    // ====================================================================
    @Override
    public void onModeChanged(String action, String description) {
        appendLog("📡 BROADCAST RECEIVED\n   " + description);
        refreshStatus();
    }

    // ====================================================================
    // refreshStatus() — reads current device state and updates all UI
    // ====================================================================
    private void refreshStatus() {
        // ── Airplane Mode ─────────────────────────────────────────────────
        // Settings.Global.AIRPLANE_MODE_ON returns 1 (ON) or 0 (OFF)
        int airplaneMode = Settings.Global.getInt(
                getContentResolver(),
                Settings.Global.AIRPLANE_MODE_ON, 0);
        boolean isAirplane = (airplaneMode != 0);

        tvAirplaneStatus.setText(isAirplane ? "ON ✈️" : "OFF");
        tvAirplaneStatus.setTextColor(isAirplane ? 0xFFFFCC80 : 0xFFEF9A9A);

        // ── Ringer Mode ───────────────────────────────────────────────────
        int ringerMode = audioManager.getRingerMode();
        switch (ringerMode) {
            case AudioManager.RINGER_MODE_NORMAL:
                tvRingerMode.setText("LOUD 🔊");
                tvRingerMode.setTextColor(0xFF80CBC4);
                applyModeUI("LOUD");
                break;
            case AudioManager.RINGER_MODE_SILENT:
                tvRingerMode.setText("SILENT 🔇");
                tvRingerMode.setTextColor(0xFFCE93D8);
                applyModeUI("SILENT");
                break;
            case AudioManager.RINGER_MODE_VIBRATE:
                tvRingerMode.setText("VIBRATE 📳");
                tvRingerMode.setTextColor(0xFFA5D6A7);
                applyModeUI("VIBRATE");
                break;
        }

        // ── Ring Volume ───────────────────────────────────────────────────
        // getStreamVolume(STREAM_RING) — ring stream
        // getStreamMaxVolume(STREAM_RING) — maximum value
        int vol    = audioManager.getStreamVolume(AudioManager.STREAM_RING);
        int maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
        tvVolume.setText(vol + " / " + maxVol);
    }

    // ====================================================================
    // applyModeUI() — updates the large icon and header text for current mode
    // ====================================================================
    private void applyModeUI(String mode) {
        switch (mode) {
            case "LOUD":
                tvModeIcon.setText("🔊");
                tvCurrentMode.setText("Current Mode: LOUD (Normal)");
                tvCurrentMode.setTextColor(0xFF80CBC4);
                break;
            case "SILENT":
                tvModeIcon.setText("🔇");
                tvCurrentMode.setText("Current Mode: SILENT");
                tvCurrentMode.setTextColor(0xFFCE93D8);
                break;
            case "VIBRATE":
                tvModeIcon.setText("📳");
                tvCurrentMode.setText("Current Mode: VIBRATE");
                tvCurrentMode.setTextColor(0xFFA5D6A7);
                break;
        }
    }

    // ====================================================================
    // sendCustomBroadcast() — sends an app-defined custom broadcast
    //
    // sendBroadcast(Intent) sends a broadcast that ANY registered receiver
    // listening for "com.example.practical21.ACTION_CUSTOM_MODE" will receive.
    //
    // This demonstrates how apps can create their own broadcast events.
    // ====================================================================
    private void sendCustomBroadcast(String message) {
        Intent customIntent = new Intent("com.example.practical21.ACTION_CUSTOM_MODE");
        customIntent.putExtra("MODE_MESSAGE", message);
        // setPackage() restricts broadcast to our own app (security best practice)
        customIntent.setPackage(getPackageName());
        sendBroadcast(customIntent);
    }

    // ====================================================================
    // appendLog() — adds a timestamped entry to the receiver log TextView
    // ====================================================================
    private void appendLog(String message) {
        String time = new SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                .format(new Date());
        receiverLog.insert(0,
                "[" + time + "]  " + message + "\n─────────────────\n");
        tvReceiverLog.setText(receiverLog.toString());
    }

    // ====================================================================
    // onResume() — refresh status when Activity comes to foreground
    // ====================================================================
    @Override
    protected void onResume() {
        super.onResume();
        refreshStatus();
    }

    // ====================================================================
    // onDestroy() — MUST unregister the BroadcastReceiver
    //
    // Failing to call unregisterReceiver() causes a memory/context leak:
    //   "Activity has leaked IntentReceiver that was originally registered here"
    //
    // Always pair registerReceiver() with unregisterReceiver().
    // ====================================================================
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove the callback to avoid memory leaks
        ModeReceiver.setListener(null);
        // Unregister the BroadcastReceiver from the system
        unregisterReceiver(modeReceiver);
        appendLog("BroadcastReceiver unregistered (onDestroy)");
    }
}