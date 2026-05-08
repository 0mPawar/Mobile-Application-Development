package com.example.viewandedittext;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    // Declare UI component variables
    TextView tvOutput;
    EditText etName, etEmail;
    AutoCompleteTextView actvCity;
    Button btnSubmit;

    // List of cities for AutoCompleteTextView suggestions
    String[] cities = {
            "Mumbai", "Pune", "Delhi", "Bangalore", "Chennai",
            "Hyderabad", "Kolkata", "Ahmedabad", "Jaipur", "Surat",
            "Lucknow", "Nagpur", "Indore", "Bhopal", "Patna"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the layout for this Activity
        setContentView(R.layout.activity_main);

        // Step 1: Link XML components to Java using findViewById()
        tvOutput  = findViewById(R.id.tvOutput);
        etName    = findViewById(R.id.etName);
        etEmail   = findViewById(R.id.etEmail);
        actvCity  = findViewById(R.id.actvCity);
        btnSubmit = findViewById(R.id.btnSubmit);

        // Step 2: Set up ArrayAdapter for AutoCompleteTextView
        // ArrayAdapter provides the list of suggestions from the cities array
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                cities
        );
        actvCity.setAdapter(adapter);

        // Step 3: Event Handling — respond to button click
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Read input values from EditText and AutoCompleteTextView
                String name  = etName.getText().toString().trim();
                String city  = actvCity.getText().toString().trim();
                String email = etEmail.getText().toString().trim();

                // Step 4: Input Validation — check if fields are empty
                if (name.isEmpty()) {
                    etName.setError("Name cannot be empty");
                    etName.requestFocus();
                    return;
                }

                if (city.isEmpty()) {
                    actvCity.setError("City cannot be empty");
                    actvCity.requestFocus();
                    return;
                }

                if (email.isEmpty()) {
                    etEmail.setError("Email cannot be empty");
                    etEmail.requestFocus();
                    return;
                }

                // Step 5: Display the submitted details in the output TextView
                String result = "Name  : " + name  + "\n"
                        + "City  : " + city  + "\n"
                        + "Email : " + email;

                tvOutput.setText(result);

                // Show a short confirmation message (Toast)
                Toast.makeText(MainActivity.this,
                        "Details submitted successfully!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}