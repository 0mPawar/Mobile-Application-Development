package com.example.checkboxandradiobutton;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // ── CheckBox declarations (multiple selection allowed) ──────────────
    CheckBox cbReading, cbCoding, cbTravelling, cbGaming;

    // ── RadioGroup & RadioButton declarations ───────────────────────────
    // RadioGroup: Gender (single selection only)
    RadioGroup rgGender;
    RadioButton rbMale, rbFemale, rbOther;

    // RadioGroup: Course (single selection only)
    RadioGroup rgCourse;
    RadioButton rbCM, rbIT, rbETC;

    // ── Buttons & Output ────────────────────────────────────────────────
    Button btnSubmit, btnReset;
    TextView tvOutput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ----------------------------------------------------------------
        // Step 1: Link XML components to Java using findViewById()
        // ----------------------------------------------------------------

        // CheckBoxes
        cbReading = findViewById(R.id.cbReading);
        cbCoding = findViewById(R.id.cbCoding);
        cbTravelling = findViewById(R.id.cbTravelling);
        cbGaming = findViewById(R.id.cbGaming);

        // Gender RadioGroup + buttons
        rgGender = findViewById(R.id.rgGender);
        rbMale = findViewById(R.id.rbMale);
        rbFemale = findViewById(R.id.rbFemale);
        rbOther = findViewById(R.id.rbOther);

        // Course RadioGroup + buttons
        rgCourse = findViewById(R.id.rgCourse);
        rbCM = findViewById(R.id.rbCM);
        rbIT = findViewById(R.id.rbIT);
        rbETC = findViewById(R.id.rbETC);

        // Buttons and output
        btnSubmit = findViewById(R.id.btnSubmit);
        btnReset = findViewById(R.id.btnReset);
        tvOutput = findViewById(R.id.tvOutput);

        // ----------------------------------------------------------------
        // Step 2: Event Handling — SUBMIT Button
        // ----------------------------------------------------------------
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // ── Read CheckBox selections ─────────────────────────────
                // isChecked() returns true if the checkbox is ticked
                StringBuilder hobbies = new StringBuilder();

                if (cbReading.isChecked()) hobbies.append("Reading, ");
                if (cbCoding.isChecked()) hobbies.append("Coding, ");
                if (cbTravelling.isChecked()) hobbies.append("Travelling, ");
                if (cbGaming.isChecked()) hobbies.append("Gaming, ");

                // Remove trailing comma and space if any hobby was selected
                String hobbiesResult;
                if (hobbies.length() > 0) {
                    hobbiesResult = hobbies.substring(0, hobbies.length() - 2);
                } else {
                    hobbiesResult = "None selected";
                }

                // ── Read RadioGroup — Gender ──────────────────────────────
                // getCheckedRadioButtonId() returns the id of the selected button
                // Returns -1 if nothing is selected
                String gender = "Not selected";
                int selectedGenderId = rgGender.getCheckedRadioButtonId();
                if (selectedGenderId != -1) {
                    RadioButton selectedGender = findViewById(selectedGenderId);
                    gender = selectedGender.getText().toString();
                }

                // ── Read RadioGroup — Course ─────────────────────────────
                String course = "Not selected";
                int selectedCourseId = rgCourse.getCheckedRadioButtonId();
                if (selectedCourseId != -1) {
                    RadioButton selectedCourse = findViewById(selectedCourseId);
                    course = selectedCourse.getText().toString();
                }

                // ── Validation: ensure at least one option per section ──
                if (hobbiesResult.equals("None selected")) {
                    Toast.makeText(MainActivity.this,
                            "Please select at least one hobby!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (gender.equals("Not selected")) {
                    Toast.makeText(MainActivity.this,
                            "Please select your gender!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (course.equals("Not selected")) {
                    Toast.makeText(MainActivity.this,
                            "Please select your course!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // ── Display results in output TextView ───────────────────
                String result =
                        "Hobbies  : " + hobbiesResult + "\n\n" +
                                "Gender   : " + gender + "\n\n" +
                                "Course   : " + course;

                tvOutput.setText(result);

                Toast.makeText(MainActivity.this,
                        "Form submitted successfully!", Toast.LENGTH_SHORT).show();
            }
        });

        // ----------------------------------------------------------------
        // Step 3: Event Handling — RESET Button
        // Clears all selections and resets the output TextView
        // ----------------------------------------------------------------
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Uncheck all CheckBoxes
                cbReading.setChecked(false);
                cbCoding.setChecked(false);
                cbTravelling.setChecked(false);
                cbGaming.setChecked(false);

                // Clear RadioGroup selections
                // clearCheck() deselects all RadioButtons in the group
                rgGender.clearCheck();
                rgCourse.clearCheck();

                // Reset output text
                tvOutput.setText("(Your selections will appear here)");

                Toast.makeText(MainActivity.this,
                        "Form reset!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}