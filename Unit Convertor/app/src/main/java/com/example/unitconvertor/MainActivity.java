package com.example.unitconvertor;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    // ── Tab buttons ───────────────────────────────────────────────────────
    TextView tabTemp, tabVolume, tabCurrency;

    // ── Sections ──────────────────────────────────────────────────────────
    ScrollView sectionTemp, sectionVolume, sectionCurrency;

    // ── Temperature components ────────────────────────────────────────────
    EditText etTempInput;
    Spinner spinnerTempFrom, spinnerTempTo;
    Button btnConvertTemp;
    LinearLayout llTempResult;
    TextView tvTempResult, tvTempFormula;

    // ── Volume components ─────────────────────────────────────────────────
    EditText etVolumeInput;
    Spinner spinnerVolumeFrom, spinnerVolumeTo;
    Button btnConvertVolume;
    LinearLayout llVolumeResult;
    TextView tvVolumeResult, tvVolumeFormula;

    // ── Currency components ───────────────────────────────────────────────
    EditText etCurrencyInput;
    Spinner spinnerCurrencyFrom, spinnerCurrencyTo;
    Button btnConvertCurrency;
    LinearLayout llCurrencyResult;
    TextView tvCurrencyResult, tvCurrencyFormula;

    // ── Formatter for clean decimal output ───────────────────────────────
    DecimalFormat df = new DecimalFormat("#.####");

    // ====================================================================
    // SPINNER DATA ARRAYS
    // ====================================================================
    String[] tempUnits = {"Celsius (°C)", "Fahrenheit (°F)", "Kelvin (K)"};
    String[] volumeUnits = {"Litre (L)", "Millilitre (mL)", "Gallon (US gal)",
            "Cubic Metre (m³)", "Fluid Ounce (fl oz)"};
    String[] currencyUnits = {"USD ($)", "INR (₹)", "EUR (€)", "GBP (£)", "JPY (¥)"};

    // ====================================================================
    // CURRENCY EXCHANGE RATES relative to USD (base currency)
    // In a real app, these would be fetched from an API.
    // ====================================================================
    double[] ratesFromUSD = {
            1.0,      // USD → USD
            83.50,    // USD → INR
            0.92,     // USD → EUR
            0.79,     // USD → GBP
            149.50    // USD → JPY
    };
    // Conversion factors: 1 unit = X litres
    double[] toLitreFactor = {
            1.0,          // Litre → Litre
            0.001,        // mL → Litre
            3.78541,      // Gallon → Litre
            1000.0,       // m³ → Litre
            0.0295735     // fl oz → Litre
    };

    // ====================================================================
    // TEMPERATURE CONVERSION LOGIC
    // All conversions use Celsius as the intermediate (base) unit.
    // Formula: input → Celsius → output
    // ====================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ----------------------------------------------------------------
        // Step 1: Link all XML components to Java using findViewById()
        // ----------------------------------------------------------------
        tabTemp = findViewById(R.id.tabTemp);
        tabVolume = findViewById(R.id.tabVolume);
        tabCurrency = findViewById(R.id.tabCurrency);

        sectionTemp = findViewById(R.id.sectionTemp);
        sectionVolume = findViewById(R.id.sectionVolume);
        sectionCurrency = findViewById(R.id.sectionCurrency);

        // Temperature
        etTempInput = findViewById(R.id.etTempInput);
        spinnerTempFrom = findViewById(R.id.spinnerTempFrom);
        spinnerTempTo = findViewById(R.id.spinnerTempTo);
        btnConvertTemp = findViewById(R.id.btnConvertTemp);
        llTempResult = findViewById(R.id.llTempResult);
        tvTempResult = findViewById(R.id.tvTempResult);
        tvTempFormula = findViewById(R.id.tvTempFormula);

        // Volume
        etVolumeInput = findViewById(R.id.etVolumeInput);
        spinnerVolumeFrom = findViewById(R.id.spinnerVolumeFrom);
        spinnerVolumeTo = findViewById(R.id.spinnerVolumeTo);
        btnConvertVolume = findViewById(R.id.btnConvertVolume);
        llVolumeResult = findViewById(R.id.llVolumeResult);
        tvVolumeResult = findViewById(R.id.tvVolumeResult);
        tvVolumeFormula = findViewById(R.id.tvVolumeFormula);

        // Currency
        etCurrencyInput = findViewById(R.id.etCurrencyInput);
        spinnerCurrencyFrom = findViewById(R.id.spinnerCurrencyFrom);
        spinnerCurrencyTo = findViewById(R.id.spinnerCurrencyTo);
        btnConvertCurrency = findViewById(R.id.btnConvertCurrency);
        llCurrencyResult = findViewById(R.id.llCurrencyResult);
        tvCurrencyResult = findViewById(R.id.tvCurrencyResult);
        tvCurrencyFormula = findViewById(R.id.tvCurrencyFormula);

        // ----------------------------------------------------------------
        // Step 2: Populate Spinners using ArrayAdapter
        //         ArrayAdapter bridges the String[] array to Spinner UI
        // ----------------------------------------------------------------
        setupSpinner(spinnerTempFrom, tempUnits);
        setupSpinner(spinnerTempTo, tempUnits);
        setupSpinner(spinnerVolumeFrom, volumeUnits);
        setupSpinner(spinnerVolumeTo, volumeUnits);
        setupSpinner(spinnerCurrencyFrom, currencyUnits);
        setupSpinner(spinnerCurrencyTo, currencyUnits);

        // Set default "To" selections to avoid same-unit conversions
        spinnerTempTo.setSelection(1);      // default: Fahrenheit
        spinnerVolumeTo.setSelection(2);    // default: Gallon
        spinnerCurrencyTo.setSelection(1);  // default: INR

        // ----------------------------------------------------------------
        // Step 3: Tab click listeners
        // ----------------------------------------------------------------
        tabTemp.setOnClickListener(v -> switchTab(0));
        tabVolume.setOnClickListener(v -> switchTab(1));
        tabCurrency.setOnClickListener(v -> switchTab(2));

        // ----------------------------------------------------------------
        // Step 4: TEMPERATURE Convert button
        // ----------------------------------------------------------------
        btnConvertTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = etTempInput.getText().toString().trim();
                if (input.isEmpty()) {
                    Toast.makeText(MainActivity.this,
                            "Please enter a temperature value", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Double.parseDouble() converts String input to a double value
                double value = Double.parseDouble(input);
                int fromIndex = spinnerTempFrom.getSelectedItemPosition();
                int toIndex = spinnerTempTo.getSelectedItemPosition();

                // Convert input unit → Celsius as intermediate (base unit)
                double celsius = toCelsius(value, fromIndex);

                // Convert Celsius → target unit
                double result = fromCelsius(celsius, toIndex);

                // Get display symbols for the formula line
                String fromSymbol = getTempSymbol(fromIndex);
                String toSymbol = getTempSymbol(toIndex);

                String formulaLine = buildTempFormula(value, fromIndex, toIndex, result);

                // Display result
                tvTempResult.setText(df.format(result) + " " + toSymbol);
                tvTempFormula.setText(formulaLine);
                llTempResult.setVisibility(View.VISIBLE);
            }
        });

        // ----------------------------------------------------------------
        // Step 5: VOLUME Convert button
        // ----------------------------------------------------------------
        btnConvertVolume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = etVolumeInput.getText().toString().trim();
                if (input.isEmpty()) {
                    Toast.makeText(MainActivity.this,
                            "Please enter a volume value", Toast.LENGTH_SHORT).show();
                    return;
                }

                double value = Double.parseDouble(input);
                int fromIndex = spinnerVolumeFrom.getSelectedItemPosition();
                int toIndex = spinnerVolumeTo.getSelectedItemPosition();

                // Convert input → Litres (base) → target unit
                double litres = toLitres(value, fromIndex);
                double result = fromLitres(litres, toIndex);

                String fromName = volumeUnits[fromIndex];
                String toName = volumeUnits[toIndex];

                tvVolumeResult.setText(df.format(result) + " " + getVolumeShort(toIndex));
                tvVolumeFormula.setText(df.format(value) + " " + getVolumeShort(fromIndex)
                        + "  →  " + df.format(result) + " " + getVolumeShort(toIndex));
                llVolumeResult.setVisibility(View.VISIBLE);
            }
        });

        // ----------------------------------------------------------------
        // Step 6: CURRENCY Convert button
        //
        // Formula: result = (amount / rateFrom) × rateTo
        // where rates are relative to USD as base currency.
        // ----------------------------------------------------------------
        btnConvertCurrency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = etCurrencyInput.getText().toString().trim();
                if (input.isEmpty()) {
                    Toast.makeText(MainActivity.this,
                            "Please enter an amount", Toast.LENGTH_SHORT).show();
                    return;
                }

                double amount = Double.parseDouble(input);
                int fromIndex = spinnerCurrencyFrom.getSelectedItemPosition();
                int toIndex = spinnerCurrencyTo.getSelectedItemPosition();

                // Step 1: Convert from source currency to USD
                double amountInUSD = amount / ratesFromUSD[fromIndex];
                // Step 2: Convert USD to target currency
                double result = amountInUSD * ratesFromUSD[toIndex];

                String fromSymbol = getCurrencySymbol(fromIndex);
                String toSymbol = getCurrencySymbol(toIndex);

                // Exchange rate shown: how much 1 unit of source = in target
                double rate = ratesFromUSD[toIndex] / ratesFromUSD[fromIndex];

                tvCurrencyResult.setText(toSymbol + " " + df.format(result));
                tvCurrencyFormula.setText(
                        "1 " + getCurrencyCode(fromIndex)
                                + " = " + df.format(rate) + " " + getCurrencyCode(toIndex)
                                + "\n" + fromSymbol + df.format(amount)
                                + " × " + df.format(rate) + " = " + toSymbol + df.format(result)
                );
                llCurrencyResult.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * toCelsius() — converts from any unit to Celsius
     *
     * @param value input temperature
     * @param index 0=Celsius, 1=Fahrenheit, 2=Kelvin
     */
    private double toCelsius(double value, int index) {
        switch (index) {
            case 0:
                return value;                          // C → C: no change
            case 1:
                return (value - 32) * 5.0 / 9.0;     // F → C: (F-32) × 5/9
            case 2:
                return value - 273.15;                 // K → C: K - 273.15
            default:
                return value;
        }
    }

    /**
     * fromCelsius() — converts from Celsius to any target unit
     *
     * @param celsius temperature in Celsius
     * @param index   0=Celsius, 1=Fahrenheit, 2=Kelvin
     */
    private double fromCelsius(double celsius, int index) {
        switch (index) {
            case 0:
                return celsius;                            // C → C
            case 1:
                return (celsius * 9.0 / 5.0) + 32;       // C → F: (C×9/5)+32
            case 2:
                return celsius + 273.15;                   // C → K: C+273.15
            default:
                return celsius;
        }
    }

    private String getTempSymbol(int index) {
        String[] symbols = {"°C", "°F", "K"};
        return symbols[index];
    }

    // ====================================================================
    // VOLUME CONVERSION LOGIC
    // All conversions use Litres as the intermediate (base) unit.
    // ====================================================================

    private String buildTempFormula(double val, int from, int to, double result) {
        String f = getTempSymbol(from);
        String t = getTempSymbol(to);
        if (from == 0 && to == 1)
            return "(" + df.format(val) + " × 9/5) + 32 = " + df.format(result) + t;
        if (from == 1 && to == 0)
            return "(" + df.format(val) + " − 32) × 5/9 = " + df.format(result) + t;
        if (from == 0 && to == 2)
            return df.format(val) + " + 273.15 = " + df.format(result) + t;
        if (from == 2 && to == 0)
            return df.format(val) + " − 273.15 = " + df.format(result) + t;
        return df.format(val) + f + "  →  " + df.format(result) + t;
    }

    private double toLitres(double value, int index) {
        return value * toLitreFactor[index];
    }

    private double fromLitres(double litres, int index) {
        return litres / toLitreFactor[index];
    }

    private String getVolumeShort(int index) {
        String[] shorts = {"L", "mL", "gal", "m³", "fl oz"};
        return shorts[index];
    }

    // ====================================================================
    // CURRENCY HELPERS
    // ====================================================================

    private String getCurrencySymbol(int index) {
        String[] symbols = {"$", "₹", "€", "£", "¥"};
        return symbols[index];
    }

    private String getCurrencyCode(int index) {
        String[] codes = {"USD", "INR", "EUR", "GBP", "JPY"};
        return codes[index];
    }

    // ====================================================================
    // SPINNER SETUP HELPER
    // ArrayAdapter: bridges a String[] data source to a Spinner widget.
    // simple_spinner_item      — layout for the closed/selected item view
    // simple_spinner_dropdown_item — layout for the expanded dropdown list
    // ====================================================================
    private void setupSpinner(Spinner spinner, String[] items) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                items
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    // ====================================================================
    // TAB SWITCHING — show/hide the 3 sections
    // ====================================================================
    private void switchTab(int index) {
        sectionTemp.setVisibility(View.GONE);
        sectionVolume.setVisibility(View.GONE);
        sectionCurrency.setVisibility(View.GONE);

        tabTemp.setTextColor(0xFF888888);
        tabTemp.setBackgroundColor(0xFFFFFFFF);
        tabVolume.setTextColor(0xFF888888);
        tabVolume.setBackgroundColor(0xFFFFFFFF);
        tabCurrency.setTextColor(0xFF888888);
        tabCurrency.setBackgroundColor(0xFFFFFFFF);

        if (index == 0) {
            sectionTemp.setVisibility(View.VISIBLE);
            tabTemp.setTextColor(0xFF3F51B5);
            tabTemp.setBackgroundColor(0xFFE8EAF6);
        } else if (index == 1) {
            sectionVolume.setVisibility(View.VISIBLE);
            tabVolume.setTextColor(0xFF009688);
            tabVolume.setBackgroundColor(0xFFE0F7FA);
        } else {
            sectionCurrency.setVisibility(View.VISIBLE);
            tabCurrency.setTextColor(0xFFF57F17);
            tabCurrency.setBackgroundColor(0xFFFFF8E1);
        }
    }
}