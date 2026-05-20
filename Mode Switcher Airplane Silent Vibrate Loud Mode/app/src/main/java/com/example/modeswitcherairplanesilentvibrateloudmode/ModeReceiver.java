package com.example.modeswitcherairplanesilentvibrateloudmode;
// ModeReceiver.java
// ====================================================================
// A BroadcastReceiver is a component that responds to system-wide
// or app-level broadcast messages.
//
// To create a BroadcastReceiver:
//   1. Extend the BroadcastReceiver class
//   2. Override onReceive(Context context, Intent intent)
//   3. Register it — either in AndroidManifest.xml (static)
//                    or via registerReceiver() in Activity (dynamic)
//
// This receiver listens for:
//   - Airplane mode toggled  (Intent.ACTION_AIRPLANE_MODE_CHANGED)
//   - Ringer mode changed    (AudioManager.ACTION_RINGER_MODE_CHANGED)
//   - Screen turned ON/OFF   (Intent.ACTION_SCREEN_ON / ACTION_SCREEN_OFF)
//   - Power connected/disconnected (ACTION_POWER_CONNECTED / DISCONNECTED)
//   - Custom broadcast from  (MainActivity sends ACTION_CUSTOM_MODE)
// ====================================================================

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.widget.Toast;

public class ModeReceiver extends BroadcastReceiver {

    // ── Interface to communicate events back to MainActivity ─────────────
    // Since BroadcastReceiver has no direct reference to the Activity,
    // we use a callback interface to pass data back.
    public interface ModeChangeListener {
        void onModeChanged(String action, String description);
    }

    // Static reference — set by MainActivity when registering
    private static ModeChangeListener listener;

    public static void setListener(ModeChangeListener l) {
        listener = l;
    }

    // ====================================================================
    // onReceive() — called by Android when a matching broadcast is received
    //
    // Parameters:
    //   context — the Context in which the receiver is running
    //   intent  — the Intent that triggered this receiver
    //             intent.getAction() tells us WHICH broadcast fired
    //
    // IMPORTANT: onReceive() runs on the MAIN thread.
    //            Do NOT do heavy work here — use a Service if needed.
    //            This method has a 10-second timeout; after that Android
    //            considers the app unresponsive.
    // ====================================================================
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null) return;
        boolean isOn = intent.getBooleanExtra("state", false);
        String description = "";

        switch (action) {

            // ── AIRPLANE MODE CHANGED ─────────────────────────────────────
            // Fired by the system whenever the user toggles Airplane Mode.
            // intent.getBooleanExtra(Intent.EXTRA_AIRPLANE_STATE, false)
            //   true  = Airplane Mode turned ON
            //   false = Airplane Mode turned OFF
            case Intent.ACTION_AIRPLANE_MODE_CHANGED:
                boolean isAirplaneOn = intent.getBooleanExtra(
                        "state", false);
                if (isAirplaneOn) {
                    description = "✈️ Airplane Mode: ON\n   All radios disabled.";
                    Toast.makeText(context,
                            "✈️ Airplane Mode turned ON",
                            Toast.LENGTH_SHORT).show();
                } else {
                    description = "✈️ Airplane Mode: OFF\n   Connectivity restored.";
                    Toast.makeText(context,
                            "✈️ Airplane Mode turned OFF",
                            Toast.LENGTH_SHORT).show();
                }
                break;

            // ── RINGER MODE CHANGED ───────────────────────────────────────
            // Fired when the ringer mode changes (Silent / Vibrate / Normal).
            // AudioManager.EXTRA_RINGER_MODE carries the new mode value.
            case AudioManager.RINGER_MODE_CHANGED_ACTION:
                int ringerMode = intent.getIntExtra(
                        AudioManager.EXTRA_RINGER_MODE,
                        AudioManager.RINGER_MODE_NORMAL);
                switch (ringerMode) {
                    case AudioManager.RINGER_MODE_SILENT:
                        description = "🔇 Ringer Mode: SILENT\n   No ring or vibration.";
                        Toast.makeText(context, "🔇 Silent Mode ON",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case AudioManager.RINGER_MODE_VIBRATE:
                        description = "📳 Ringer Mode: VIBRATE\n   Vibration only.";
                        Toast.makeText(context, "📳 Vibrate Mode ON",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case AudioManager.RINGER_MODE_NORMAL:
                        description = "🔊 Ringer Mode: NORMAL (LOUD)\n   Full ring and sound.";
                        Toast.makeText(context, "🔊 Loud Mode ON",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
                break;

            // ── SCREEN ON ─────────────────────────────────────────────────
            case Intent.ACTION_SCREEN_ON:
                description = "📱 Screen: ON";
                break;

            // ── SCREEN OFF ────────────────────────────────────────────────
            case Intent.ACTION_SCREEN_OFF:
                description = "📱 Screen: OFF";
                break;

            // ── POWER CONNECTED ───────────────────────────────────────────
            case Intent.ACTION_POWER_CONNECTED:
                description = "🔌 Power: CONNECTED (Charging)";
                Toast.makeText(context, "🔌 Charger connected",
                        Toast.LENGTH_SHORT).show();
                break;

            // ── POWER DISCONNECTED ────────────────────────────────────────
            case Intent.ACTION_POWER_DISCONNECTED:
                description = "🔋 Power: DISCONNECTED (Unplugged)";
                Toast.makeText(context, "🔋 Charger unplugged",
                        Toast.LENGTH_SHORT).show();
                break;

            // ── CUSTOM BROADCAST from MainActivity ────────────────────────
            case "com.example.practical21.ACTION_CUSTOM_MODE":
                String customMsg = intent.getStringExtra("MODE_MESSAGE");
                description = "📣 Custom Broadcast:\n   " + customMsg;
                break;

            default:
                description = "📡 Unknown action: " + action;
        }

        // Notify the Activity via the callback interface
        if (listener != null && !description.isEmpty()) {
            listener.onModeChanged(action, description);
        }
    }
}