package com.example.customtoastalert;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // ── Personal Details (LinearLayout section) ──────────────────────────
    EditText etFullName, etEmail, etPhone, etDOB, etAge;

    // ── Academic Details (ConstraintLayout section) ───────────────────────
    EditText etCourse, etYear, etDivision;

    // ── Gender RadioGroup ─────────────────────────────────────────────────
    RadioGroup rgGender;

    // ── Buttons ───────────────────────────────────────────────────────────
    Button btnRegister, btnReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ----------------------------------------------------------------
        // Step 1: Link all XML views to Java using findViewById()
        // ----------------------------------------------------------------
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etDOB = findViewById(R.id.etDOB);
        etAge = findViewById(R.id.etAge);

        etCourse = findViewById(R.id.etCourse);
        etYear = findViewById(R.id.etYear);
        etDivision = findViewById(R.id.etDivision);

        rgGender = findViewById(R.id.rgGender);

        btnRegister = findViewById(R.id.btnRegister);
        btnReset = findViewById(R.id.btnReset);

        // ----------------------------------------------------------------
        // Step 2: REGISTER button — validate all fields then show Toast
        // ----------------------------------------------------------------
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Read all field values
                String name = etFullName.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String phone = etPhone.getText().toString().trim();
                String dob = etDOB.getText().toString().trim();
                String age = etAge.getText().toString().trim();
                String course = etCourse.getText().toString().trim();
                String year = etYear.getText().toString().trim();
                String division = etDivision.getText().toString().trim();

                // ── Validation checks ───────────────────────────────────

                if (name.isEmpty()) {
                    etFullName.setError("Full name is required");
                    etFullName.requestFocus();
                    showCustomToast("Error!", "Full name cannot be empty.", false);
                    return;
                }

                if (email.isEmpty() || !email.contains("@")) {
                    etEmail.setError("Valid email is required");
                    etEmail.requestFocus();
                    showCustomToast("Error!", "Enter a valid email address.", false);
                    return;
                }

                if (phone.isEmpty() || phone.length() != 10) {
                    etPhone.setError("Enter a valid 10-digit phone number");
                    etPhone.requestFocus();
                    showCustomToast("Error!", "Phone must be 10 digits.", false);
                    return;
                }

                if (dob.isEmpty()) {
                    etDOB.setError("Date of birth is required");
                    etDOB.requestFocus();
                    showCustomToast("Error!", "Enter your date of birth.", false);
                    return;
                }

                if (age.isEmpty()) {
                    etAge.setError("Age is required");
                    etAge.requestFocus();
                    showCustomToast("Error!", "Age cannot be empty.", false);
                    return;
                }

                // Gender validation — getCheckedRadioButtonId() returns -1 if none selected
                if (rgGender.getCheckedRadioButtonId() == -1) {
                    showCustomToast("Error!", "Please select your gender.", false);
                    return;
                }

                if (course.isEmpty()) {
                    etCourse.setError("Course is required");
                    etCourse.requestFocus();
                    showCustomToast("Error!", "Enter your course name.", false);
                    return;
                }

                if (year.isEmpty()) {
                    etYear.setError("Year is required");
                    etYear.requestFocus();
                    showCustomToast("Error!", "Enter your current year.", false);
                    return;
                }

                if (division.isEmpty()) {
                    etDivision.setError("Division is required");
                    etDivision.requestFocus();
                    showCustomToast("Error!", "Enter your division.", false);
                    return;
                }

                // ── All validations passed ──────────────────────────────
                // Read selected gender text
                RadioButton selectedGender = findViewById(rgGender.getCheckedRadioButtonId());
                String gender = selectedGender.getText().toString();

                // Show SUCCESS custom Toast
                showCustomToast(
                        "Registration Successful! 🎉",
                        name + " registered as " + gender + " in " + course + ", " + year,
                        true
                );
            }
        });

        // ----------------------------------------------------------------
        // Step 3: RESET button — clear all input fields
        // ----------------------------------------------------------------
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etFullName.setText("");
                etEmail.setText("");
                etPhone.setText("");
                etDOB.setText("");
                etAge.setText("");
                etCourse.setText("");
                etYear.setText("");
                etDivision.setText("");
                rgGender.clearCheck();

                showCustomToast("Form Cleared", "All fields have been reset.", true);
            }
        });
    }

    // ====================================================================
    // showCustomToast() — Helper method to display a styled Custom Toast
    //
    // How it works:
    //   1. LayoutInflater inflates (creates a View from) our custom_toast.xml
    //   2. We set the icon, title, and message text dynamically
    //   3. We change background color: green for success, red for error
    //   4. Toast.setView() replaces the default Toast UI with our layout
    //   5. setGravity() positions the Toast at the bottom-center of screen
    //
    // Parameters:
    //   title   — bold heading in the Toast (e.g. "Success!" or "Error!")
    //   message — smaller detail text
    //   success — true = green background, false = red background
    // ====================================================================
    private void showCustomToast(String title, String message, boolean success) {

        // Step A: Inflate the custom_toast.xml layout into a View object
        LayoutInflater inflater = getLayoutInflater();
        View toastView = inflater.inflate(
                R.layout.custom_toast,
                findViewById(android.R.id.content),
                false
        );

        // Step B: Find the views inside the inflated layout
        LinearLayout toastRoot = toastView.findViewById(R.id.toastRoot);
        TextView tvToastIcon = toastView.findViewById(R.id.tvToastIcon);
        TextView tvToastTitle = toastView.findViewById(R.id.tvToastTitle);
        TextView tvToastMsg = toastView.findViewById(R.id.tvToastMessage);

        // Step C: Set content dynamically
        tvToastTitle.setText(title);
        tvToastMsg.setText(message);

        if (success) {
            // Green background for success
            tvToastIcon.setText("✅");
            toastRoot.setBackgroundColor(0xFF2E7D32);   // dark green
        } else {
            // Red background for error
            tvToastIcon.setText("❌");
            toastRoot.setBackgroundColor(0xFFC62828);   // dark red
        }

        // Step D: Create Toast, assign custom view, position it, show it
        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_LONG);

        // setView() replaces the default Toast UI with our custom layout
        toast.setView(toastView);

        // setGravity() positions the Toast: bottom-center with 100dp offset from bottom
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 100);

        toast.show();
    }
}