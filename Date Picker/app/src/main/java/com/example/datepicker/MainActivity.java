package com.example.datepicker;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    // ── Section 1: Inline DatePicker ─────────────────────────────────────
    DatePicker datePickerInline;
    Button     btnConfirmInline;
    TextView   tvInlineResult;

    // ── Section 2: DatePickerDialog ──────────────────────────────────────
    Button   btnOpenDialog;
    TextView tvDialogSelectedDate, tvDialogResult;

    // ── Section 3: Date Range (Check-in / Check-out) ─────────────────────
    Button   btnCheckIn, btnCheckOut, btnCheckRange;
    TextView tvCheckIn, tvCheckOut, tvRangeResult;

    // ── Stored range dates ────────────────────────────────────────────────
    // Calendar objects hold the selected check-in and check-out dates
    Calendar calCheckIn  = null;
    Calendar calCheckOut = null;

    // ── Month names array for display ─────────────────────────────────────
    // Calendar.MONTH is 0-indexed (0 = January), so we use this array
    // to convert the integer month value to a readable string.
    static final String[] MONTHS = {
            "January","February","March","April","May","June",
            "July","August","September","October","November","December"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ----------------------------------------------------------------
        // Step 1: Link all XML components to Java using findViewById()
        // ----------------------------------------------------------------

        // Section 1 — Inline
        datePickerInline  = findViewById(R.id.datePickerInline);
        btnConfirmInline  = findViewById(R.id.btnConfirmInline);
        tvInlineResult    = findViewById(R.id.tvInlineResult);

        // Section 2 — Dialog
        btnOpenDialog        = findViewById(R.id.btnOpenDialog);
        tvDialogSelectedDate = findViewById(R.id.tvDialogSelectedDate);
        tvDialogResult       = findViewById(R.id.tvDialogResult);

        // Section 3 — Range
        btnCheckIn   = findViewById(R.id.btnCheckIn);
        btnCheckOut  = findViewById(R.id.btnCheckOut);
        btnCheckRange= findViewById(R.id.btnCheckRange);
        tvCheckIn    = findViewById(R.id.tvCheckIn);
        tvCheckOut   = findViewById(R.id.tvCheckOut);
        tvRangeResult= findViewById(R.id.tvRangeResult);

        // ----------------------------------------------------------------
        // Step 2: Set the inline DatePicker's initial date to TODAY
        //         Calendar.getInstance() returns the current date/time.
        //         DatePicker.init() sets day, month, year + listener.
        // ----------------------------------------------------------------
        Calendar today = Calendar.getInstance();
        int todayYear  = today.get(Calendar.YEAR);
        int todayMonth = today.get(Calendar.MONTH);   // 0-indexed
        int todayDay   = today.get(Calendar.DAY_OF_MONTH);

        /*
         * DatePicker.init(year, monthOfYear, dayOfMonth, listener)
         *   year        — 4-digit year
         *   monthOfYear — 0 (Jan) to 11 (Dec) — NOTE: 0-indexed!
         *   dayOfMonth  — 1 to 31
         *   listener    — OnDateChangedListener, fires on every change
         *                 (we use null here; we read values on button click)
         */
        datePickerInline.init(todayYear, todayMonth, todayDay, null);

        // ----------------------------------------------------------------
        // Step 3: Inline DatePicker — CONFIRM button
        //         Reads day/month/year from the DatePicker widget.
        //         DatePicker.getYear(), getMonth(), getDayOfMonth()
        // ----------------------------------------------------------------
        btnConfirmInline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Read values from the inline DatePicker widget
                int year  = datePickerInline.getYear();
                int month = datePickerInline.getMonth();        // 0-indexed
                int day   = datePickerInline.getDayOfMonth();

                // Format the date for display
                String formatted = formatDate(day, month, year);
                String dayName   = getDayName(year, month, day);

                tvInlineResult.setText(
                        "✅  Selected Date:\n" + dayName + ", " + formatted
                );
                tvInlineResult.setTextColor(0xFF3F51B5);

                Toast.makeText(MainActivity.this,
                        "Date confirmed: " + formatted, Toast.LENGTH_SHORT).show();
            }
        });

        // ----------------------------------------------------------------
        // Step 4: DatePickerDialog — open on button click
        //
        // DatePickerDialog(context, listener, year, month, day)
        //   context  — current Activity
        //   listener — OnDateSetListener: called when user taps "Set/OK"
        //   year, month (0-indexed), day — initial date shown in dialog
        //
        // The dialog shown matches the screenshot style:
        //   "Pick a date" title with Day / Month / Year scroll wheels,
        //   and Cancel / Clear / Set buttons at the bottom.
        // ----------------------------------------------------------------
        btnOpenDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal   = Calendar.getInstance();
                int year       = cal.get(Calendar.YEAR);
                int month      = cal.get(Calendar.MONTH);
                int day        = cal.get(Calendar.DAY_OF_MONTH);

                // Create DatePickerDialog
                DatePickerDialog dialog = new DatePickerDialog(
                        MainActivity.this,

                        // OnDateSetListener — called when user taps "Set"
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view,
                                                  int selectedYear,
                                                  int selectedMonth,  // 0-indexed
                                                  int selectedDay) {

                                String formatted = formatDate(selectedDay,
                                        selectedMonth, selectedYear);
                                String dayName = getDayName(selectedYear,
                                        selectedMonth, selectedDay);

                                // Update the display field and result card
                                tvDialogSelectedDate.setText(
                                        dayName + ", " + formatted);
                                tvDialogSelectedDate.setTextColor(0xFF222222);

                                tvDialogResult.setText(
                                        "✅  Selected Date:\n"
                                                + dayName + ", " + formatted);
                                tvDialogResult.setTextColor(0xFF009688);

                                Toast.makeText(MainActivity.this,
                                        "Date set: " + formatted,
                                        Toast.LENGTH_SHORT).show();
                            }
                        },
                        year, month, day   // initial date for the dialog
                );

                // Set the dialog title (matches "Pick a date" in screenshot)
                dialog.setTitle("Pick a date");

                // Show the dialog
                dialog.show();
            }
        });

        // ----------------------------------------------------------------
        // Step 5: Date Range — Check-in picker
        //         Opens a DatePickerDialog; on Set, stores result in
        //         calCheckIn and updates tvCheckIn display.
        // ----------------------------------------------------------------
        btnCheckIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = (calCheckIn != null) ? calCheckIn
                        : Calendar.getInstance();
                DatePickerDialog d = new DatePickerDialog(
                        MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view,
                                                  int year, int month, int day) {
                                calCheckIn = Calendar.getInstance();
                                // Calendar.set() — sets year, month (0-indexed), day
                                calCheckIn.set(year, month, day);
                                tvCheckIn.setText(formatDate(day, month, year));
                                tvCheckIn.setTextColor(0xFF222222);
                            }
                        },
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)
                );
                d.setTitle("Check-in Date");
                d.show();
            }
        });

        // ----------------------------------------------------------------
        // Step 6: Date Range — Check-out picker
        //         Same pattern as check-in, but stores in calCheckOut.
        //         Minimum date is set to check-in date if already chosen.
        // ----------------------------------------------------------------
        btnCheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = (calCheckOut != null) ? calCheckOut
                        : Calendar.getInstance();
                DatePickerDialog d = new DatePickerDialog(
                        MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view,
                                                  int year, int month, int day) {
                                calCheckOut = Calendar.getInstance();
                                calCheckOut.set(year, month, day);
                                tvCheckOut.setText(formatDate(day, month, year));
                                tvCheckOut.setTextColor(0xFF222222);
                            }
                        },
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)
                );
                d.setTitle("Check-out Date");

                // If check-in is already set, disable dates before it
                // DatePickerDialog.getDatePicker().setMinDate(epochMs)
                // prevents selecting a checkout before checkin
                if (calCheckIn != null) {
                    d.getDatePicker().setMinDate(calCheckIn.getTimeInMillis());
                }
                d.show();
            }
        });

        // ----------------------------------------------------------------
        // Step 7: CONFIRM BOOKING — validate range and show summary
        // ----------------------------------------------------------------
        btnCheckRange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (calCheckIn == null) {
                    Toast.makeText(MainActivity.this,
                            "Please select a check-in date", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (calCheckOut == null) {
                    Toast.makeText(MainActivity.this,
                            "Please select a check-out date", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validate: check-out must be after check-in
                if (!calCheckOut.after(calCheckIn)) {
                    Toast.makeText(MainActivity.this,
                            "Check-out must be after check-in!",
                            Toast.LENGTH_SHORT).show();
                    tvRangeResult.setText("❌  Invalid range: Check-out must be after check-in.");
                    tvRangeResult.setTextColor(0xFFD32F2F);
                    return;
                }

                // Calculate number of nights
                // Difference in milliseconds → convert to days
                long diffMs    = calCheckOut.getTimeInMillis() - calCheckIn.getTimeInMillis();
                long nights    = diffMs / (1000 * 60 * 60 * 24);

                String checkIn  = formatDate(
                        calCheckIn.get(Calendar.DAY_OF_MONTH),
                        calCheckIn.get(Calendar.MONTH),
                        calCheckIn.get(Calendar.YEAR));

                String checkOut = formatDate(
                        calCheckOut.get(Calendar.DAY_OF_MONTH),
                        calCheckOut.get(Calendar.MONTH),
                        calCheckOut.get(Calendar.YEAR));

                tvRangeResult.setText(
                        "✅  Booking Confirmed!\n\n"
                                + "Check-in  :  " + checkIn  + "\n"
                                + "Check-out :  " + checkOut + "\n"
                                + "Duration  :  " + nights + " night(s)"
                );
                tvRangeResult.setTextColor(0xFF388E3C);
            }
        });
    }

    // ====================================================================
    // formatDate() — converts day / month(0-indexed) / year to display string
    //                e.g.  14, 0, 2026  →  "14 January 2026"
    // ====================================================================
    private String formatDate(int day, int month, int year) {
        return day + " " + MONTHS[month] + " " + year;
    }

    // ====================================================================
    // getDayName() — returns the weekday name for a given date
    //                Uses Calendar.DAY_OF_WEEK (1=Sun … 7=Sat)
    // ====================================================================
    private String getDayName(int year, int month, int day) {
        String[] days = {"Sunday","Monday","Tuesday","Wednesday",
                "Thursday","Friday","Saturday"};
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        return days[cal.get(Calendar.DAY_OF_WEEK) - 1];
    }
}