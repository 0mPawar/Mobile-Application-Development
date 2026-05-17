package com.example.timepicker;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    // ── Section 1: Inline TimePicker ─────────────────────────────────────
    TimePicker timePickerInline;
    Button     btnConfirmInline;
    TextView   tvInlineResult;

    // ── Section 2: 12-Hour TimePickerDialog ──────────────────────────────
    Button   btnOpenDialog12;
    TextView tvDialogSelectedTime, tvDialogResult12;

    // ── Section 3: 24-Hour TimePickerDialog ──────────────────────────────
    Button   btnOpenDialog24;
    TextView tvDialog24Selected, tvDialogResult24;

    // ── Section 4: Alarm Scheduler ───────────────────────────────────────
    Button   btnPickAlarmTime, btnSetAlarm;
    EditText etAlarmLabel;
    TextView tvAlarmTime, tvAlarmResult;

    // Stores the alarm hour and minute selected by the user
    int alarmHour   = -1;
    int alarmMinute = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ----------------------------------------------------------------
        // Step 1: Link all XML components to Java using findViewById()
        // ----------------------------------------------------------------

        // Section 1 — Inline
        timePickerInline = findViewById(R.id.timePickerInline);
        btnConfirmInline = findViewById(R.id.btnConfirmInline);
        tvInlineResult   = findViewById(R.id.tvInlineResult);

        // Section 2 — 12-hour dialog
        btnOpenDialog12       = findViewById(R.id.btnOpenDialog12);
        tvDialogSelectedTime  = findViewById(R.id.tvDialogSelectedTime);
        tvDialogResult12      = findViewById(R.id.tvDialogResult12);

        // Section 3 — 24-hour dialog
        btnOpenDialog24   = findViewById(R.id.btnOpenDialog24);
        tvDialog24Selected= findViewById(R.id.tvDialog24Selected);
        tvDialogResult24  = findViewById(R.id.tvDialogResult24);

        // Section 4 — Alarm
        btnPickAlarmTime = findViewById(R.id.btnPickAlarmTime);
        btnSetAlarm      = findViewById(R.id.btnSetAlarm);
        etAlarmLabel     = findViewById(R.id.etAlarmLabel);
        tvAlarmTime      = findViewById(R.id.tvAlarmTime);
        tvAlarmResult    = findViewById(R.id.tvAlarmResult);

        // ----------------------------------------------------------------
        // Step 2: Set inline TimePicker to CURRENT time
        //         setIs24HourView(false) = 12-hour (AM/PM) mode
        //         setIs24HourView(true)  = 24-hour mode
        //         getCurrentHour() / getCurrentMinute() are deprecated
        //         in API 23+; use setHour() / setMinute() instead.
        // ----------------------------------------------------------------
        Calendar now = Calendar.getInstance();
        int currentHour   = now.get(Calendar.HOUR_OF_DAY);  // 0–23
        int currentMinute = now.get(Calendar.MINUTE);        // 0–59

        // 12-hour mode for the inline picker
        timePickerInline.setIs24HourView(false);
        timePickerInline.setHour(currentHour);
        timePickerInline.setMinute(currentMinute);

        // ----------------------------------------------------------------
        // Step 3: Inline TimePicker — CONFIRM button
        //         Reads hour and minute from the TimePicker widget.
        //         getHour()   — returns 0–23 regardless of 12/24 mode
        //         getMinute() — returns 0–59
        // ----------------------------------------------------------------
        btnConfirmInline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // getHour() returns hour in 24h format (0–23)
                int hour   = timePickerInline.getHour();
                int minute = timePickerInline.getMinute();

                // Format for 12-hour AM/PM display
                String formatted12 = formatTime12(hour, minute);
                // Format for 24-hour display
                String formatted24 = String.format("%02d:%02d", hour, minute);

                tvInlineResult.setText(
                        "✅  Selected Time:\n"
                                + "12-Hour: " + formatted12 + "\n"
                                + "24-Hour: " + formatted24
                );
                tvInlineResult.setTextColor(0xFF3F51B5);

                Toast.makeText(MainActivity.this,
                        "Time confirmed: " + formatted12, Toast.LENGTH_SHORT).show();
            }
        });

        // ----------------------------------------------------------------
        // Step 4: TimePickerDialog — 12-hour (AM/PM) mode
        //
        // TimePickerDialog(context, listener, hourOfDay, minute, is24HourView)
        //   context      — current Activity
        //   listener     — OnTimeSetListener: called when user taps OK
        //   hourOfDay    — initial hour (0–23)
        //   minute       — initial minute (0–59)
        //   is24HourView — false = 12-hour AM/PM, true = 24-hour
        //
        // This dialog matches the screenshot:
        //   Title "Set time", "Type in time" with hour : minute and AM/PM,
        //   a clock icon at bottom-left, CANCEL and OK buttons.
        // ----------------------------------------------------------------
        btnOpenDialog12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal  = Calendar.getInstance();
                int hour      = cal.get(Calendar.HOUR_OF_DAY);
                int minute    = cal.get(Calendar.MINUTE);

                TimePickerDialog dialog = new TimePickerDialog(
                        MainActivity.this,

                        // OnTimeSetListener — called when user taps OK
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view,
                                                  int hourOfDay,   // 0–23
                                                  int minute) {

                                // Convert 24h → 12h AM/PM for display
                                String formatted12 = formatTime12(hourOfDay, minute);
                                String formatted24 = String.format("%02d:%02d",
                                        hourOfDay, minute);

                                // Update display field and result card
                                tvDialogSelectedTime.setText(formatted12);
                                tvDialogSelectedTime.setTextColor(0xFF222222);

                                tvDialogResult12.setText(
                                        "✅  Time Set (12-Hour):\n"
                                                + "Display : " + formatted12 + "\n"
                                                + "24-Hour : " + formatted24 + "\n"
                                                + "Period  : " + (hourOfDay < 12 ? "AM" : "PM")
                                );
                                tvDialogResult12.setTextColor(0xFF009688);

                                Toast.makeText(MainActivity.this,
                                        "Time set: " + formatted12,
                                        Toast.LENGTH_SHORT).show();
                            }
                        },
                        hour,
                        minute,
                        false   // false = 12-hour AM/PM mode (matches screenshot)
                );

                // Title shown at the top of the dialog — matches screenshot "Set time"
                dialog.setTitle("Set time");
                dialog.show();
            }
        });

        // ----------------------------------------------------------------
        // Step 5: TimePickerDialog — 24-hour mode
        //         is24HourView = true removes the AM/PM selector
        //         and shows hours 00–23.
        // ----------------------------------------------------------------
        btnOpenDialog24.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int hour     = cal.get(Calendar.HOUR_OF_DAY);
                int minute   = cal.get(Calendar.MINUTE);

                TimePickerDialog dialog = new TimePickerDialog(
                        MainActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view,
                                                  int hourOfDay,
                                                  int minute) {

                                // 24-hour format: HH:MM
                                String formatted24 = String.format("%02d:%02d",
                                        hourOfDay, minute);

                                tvDialog24Selected.setText(formatted24);
                                tvDialog24Selected.setTextColor(0xFF222222);

                                tvDialogResult24.setText(
                                        "✅  Time Set (24-Hour):\n"
                                                + "24-Hour : " + formatted24 + "\n"
                                                + "12-Hour : " + formatTime12(hourOfDay, minute)
                                );
                                tvDialogResult24.setTextColor(0xFF5C6BC0);

                                Toast.makeText(MainActivity.this,
                                        "Time set: " + formatted24,
                                        Toast.LENGTH_SHORT).show();
                            }
                        },
                        hour,
                        minute,
                        true    // true = 24-hour mode — no AM/PM selector
                );

                dialog.setTitle("Set time (24-Hour)");
                dialog.show();
            }
        });

        // ----------------------------------------------------------------
        // Step 6: Alarm Scheduler — PICK TIME button
        //         Opens a 12-hour dialog; stores selected hour/minute
        //         in alarmHour / alarmMinute class fields.
        // ----------------------------------------------------------------
        btnPickAlarmTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Use stored alarm time if already set, else use current time
                Calendar cal   = Calendar.getInstance();
                int initHour   = (alarmHour >= 0) ? alarmHour
                        : cal.get(Calendar.HOUR_OF_DAY);
                int initMinute = (alarmMinute >= 0) ? alarmMinute
                        : cal.get(Calendar.MINUTE);

                TimePickerDialog dialog = new TimePickerDialog(
                        MainActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view,
                                                  int hourOfDay, int minute) {
                                // Save the selected alarm time
                                alarmHour   = hourOfDay;
                                alarmMinute = minute;

                                // Update the large time display card
                                tvAlarmTime.setText(formatTime12(hourOfDay, minute));
                                tvAlarmTime.setTextColor(0xFF3F51B5);
                            }
                        },
                        initHour, initMinute, false
                );
                dialog.setTitle("Set Alarm Time");
                dialog.show();
            }
        });

        // ----------------------------------------------------------------
        // Step 7: Alarm Scheduler — SET ALARM button
        //         Validates label + time, then displays confirmation.
        //         Calculates minutes until the alarm fires.
        // ----------------------------------------------------------------
        btnSetAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String label = etAlarmLabel.getText().toString().trim();

                if (label.isEmpty()) {
                    etAlarmLabel.setError("Alarm label is required");
                    etAlarmLabel.requestFocus();
                    return;
                }
                if (alarmHour < 0) {
                    Toast.makeText(MainActivity.this,
                            "Please pick an alarm time first",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                // Calculate minutes until alarm fires
                Calendar now  = Calendar.getInstance();
                Calendar alarm = Calendar.getInstance();
                alarm.set(Calendar.HOUR_OF_DAY, alarmHour);
                alarm.set(Calendar.MINUTE, alarmMinute);
                alarm.set(Calendar.SECOND, 0);

                // If alarm time is in the past today, set it for tomorrow
                if (alarm.before(now)) {
                    alarm.add(Calendar.DAY_OF_YEAR, 1);
                }

                long diffMs      = alarm.getTimeInMillis() - now.getTimeInMillis();
                long diffMinutes = diffMs / (1000 * 60);
                long hours       = diffMinutes / 60;
                long minutes     = diffMinutes % 60;

                String timeUntil = (hours > 0)
                        ? hours + " hr " + minutes + " min"
                        : minutes + " min";

                String alarmStr  = formatTime12(alarmHour, alarmMinute);

                tvAlarmResult.setText(
                        "🔔  Alarm Set!\n\n"
                                + "Label   : " + label   + "\n"
                                + "Time    : " + alarmStr + "\n"
                                + "Rings in: " + timeUntil
                );
                tvAlarmResult.setTextColor(0xFFE53935);

                Toast.makeText(MainActivity.this,
                        "Alarm set for " + alarmStr, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ====================================================================
    // formatTime12() — converts 24-hour hour + minute to 12-hour AM/PM
    //
    //  hourOfDay: 0–23 (from TimePicker / Calendar)
    //  minute:    0–59
    //
    //  Conversion rules:
    //    hour 0  → 12 AM  (midnight)
    //    hour 1–11  → 1–11 AM
    //    hour 12    → 12 PM  (noon)
    //    hour 13–23 → 1–11 PM
    //
    //  Returns: "12:30 PM"  or  "09:05 AM"
    // ====================================================================
    private String formatTime12(int hourOfDay, int minute) {
        String period;
        int    hour12;

        if (hourOfDay == 0) {
            hour12 = 12;
            period = "AM";
        } else if (hourOfDay < 12) {
            hour12 = hourOfDay;
            period = "AM";
        } else if (hourOfDay == 12) {
            hour12 = 12;
            period = "PM";
        } else {
            hour12 = hourOfDay - 12;
            period = "PM";
        }

        // %02d pads single-digit minutes with a leading zero (e.g. 9 → "09")
        return String.format("%d:%02d %s", hour12, minute, period);
    }
}