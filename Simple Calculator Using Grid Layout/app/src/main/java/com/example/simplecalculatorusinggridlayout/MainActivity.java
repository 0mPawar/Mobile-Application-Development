package com.example.simplecalculatorusinggridlayout;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // ── Display TextViews ────────────────────────────────────────────────
    TextView tvDisplay;      // main large number display
    TextView tvExpression;   // smaller top bar showing full expression

    // ── Digit / Number Buttons ───────────────────────────────────────────
    Button btn0, btn1, btn2, btn3, btn4;
    Button btn5, btn6, btn7, btn8, btn9;
    Button btnDot;

    // ── Operator Buttons ─────────────────────────────────────────────────
    Button btnAdd, btnSubtract, btnMultiply, btnDivide;
    Button btnEquals;

    // ── Function Buttons ─────────────────────────────────────────────────
    Button btnAC, btnToggleSign, btnPercent;
    Button btnBackspace, btnSqrt, btnSquare, btnReciprocal;

    // ====================================================================
    // STATE MANAGEMENT — tracks the calculator's current state
    // ====================================================================
    String  currentInput  = "";    // string being typed by the user
    double  firstOperand  = 0;     // first number stored when operator pressed
    String  operator      = "";    // current operator: +, -, *, /
    boolean operatorPressed = false; // true after an operator button is tapped
    boolean resultShown     = false; // true after = is pressed

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ----------------------------------------------------------------
        // Step 1: Link all XML components to Java using findViewById()
        // ----------------------------------------------------------------
        tvDisplay    = findViewById(R.id.tvDisplay);
        tvExpression = findViewById(R.id.tvExpression);

        btn0 = findViewById(R.id.btn0);   btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);   btn3 = findViewById(R.id.btn3);
        btn4 = findViewById(R.id.btn4);   btn5 = findViewById(R.id.btn5);
        btn6 = findViewById(R.id.btn6);   btn7 = findViewById(R.id.btn7);
        btn8 = findViewById(R.id.btn8);   btn9 = findViewById(R.id.btn9);
        btnDot = findViewById(R.id.btnDot);

        btnAdd        = findViewById(R.id.btnAdd);
        btnSubtract   = findViewById(R.id.btnSubtract);
        btnMultiply   = findViewById(R.id.btnMultiply);
        btnDivide     = findViewById(R.id.btnDivide);
        btnEquals     = findViewById(R.id.btnEquals);

        btnAC          = findViewById(R.id.btnAC);
        btnToggleSign  = findViewById(R.id.btnToggleSign);
        btnPercent     = findViewById(R.id.btnPercent);
        btnBackspace   = findViewById(R.id.btnBackspace);
        btnSqrt        = findViewById(R.id.btnSqrt);
        btnSquare      = findViewById(R.id.btnSquare);
        btnReciprocal  = findViewById(R.id.btnReciprocal);

        // ----------------------------------------------------------------
        // Step 2: Digit button listeners — append digit to currentInput
        // ----------------------------------------------------------------
        View.OnClickListener digitListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button b = (Button) v;
                String digit = b.getText().toString();

                // If result was just shown, start fresh on new digit press
                if (resultShown) {
                    currentInput = "";
                    resultShown  = false;
                }

                // If a new number is being typed after an operator, reset display
                if (operatorPressed) {
                    currentInput     = "";
                    operatorPressed  = false;
                }

                // Prevent multiple leading zeros
                if (digit.equals("0") && currentInput.equals("0")) return;

                // Replace initial "0" with the digit (not for decimal)
                if (currentInput.equals("0") && !digit.equals(".")) {
                    currentInput = digit;
                } else {
                    currentInput += digit;
                }

                tvDisplay.setText(currentInput);
            }
        };

        btn0.setOnClickListener(digitListener);
        btn1.setOnClickListener(digitListener);
        btn2.setOnClickListener(digitListener);
        btn3.setOnClickListener(digitListener);
        btn4.setOnClickListener(digitListener);
        btn5.setOnClickListener(digitListener);
        btn6.setOnClickListener(digitListener);
        btn7.setOnClickListener(digitListener);
        btn8.setOnClickListener(digitListener);
        btn9.setOnClickListener(digitListener);

        // ----------------------------------------------------------------
        // Step 3: Decimal point — only one dot allowed per number
        // ----------------------------------------------------------------
        btnDot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (operatorPressed) {
                    currentInput    = "0";
                    operatorPressed = false;
                }
                // Prevent duplicate decimal points
                if (!currentInput.contains(".")) {
                    if (currentInput.isEmpty()) currentInput = "0";
                    currentInput += ".";
                    tvDisplay.setText(currentInput);
                }
            }
        });

        // ----------------------------------------------------------------
        // Step 4: Operator buttons (+, -, ×, ÷)
        //         Store firstOperand and the chosen operator,
        //         then wait for the second number.
        // ----------------------------------------------------------------
        View.OnClickListener operatorListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button b = (Button) v;

                // If there's already a pending operation and a second number
                // was entered, calculate intermediate result first (chaining)
                if (!operator.isEmpty() && !operatorPressed && !currentInput.isEmpty()) {
                    double secondOperand = parseInput();
                    double result = calculate(firstOperand, secondOperand, operator);
                    firstOperand = result;
                    tvDisplay.setText(formatResult(result));
                    currentInput = formatResult(result);
                } else if (!currentInput.isEmpty()) {
                    firstOperand = parseInput();
                }

                // Store the new operator
                String btnText = b.getText().toString();
                switch (btnText) {
                    case "+": operator = "+"; break;
                    case "−": operator = "-"; break;
                    case "×": operator = "*"; break;
                    case "÷": operator = "/"; break;
                }

                // Update expression bar: e.g. "12 + "
                tvExpression.setText(formatResult(firstOperand) + " " + btnText);
                operatorPressed = true;
                resultShown     = false;
            }
        };

        btnAdd.setOnClickListener(operatorListener);
        btnSubtract.setOnClickListener(operatorListener);
        btnMultiply.setOnClickListener(operatorListener);
        btnDivide.setOnClickListener(operatorListener);

        // ----------------------------------------------------------------
        // Step 5: EQUALS button
        //         Reads secondOperand, calls calculate(), displays result
        // ----------------------------------------------------------------
        btnEquals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (operator.isEmpty() || currentInput.isEmpty()) return;

                double secondOperand = parseInput();

                // Show full expression in the expression bar
                String opSymbol = getOperatorSymbol(operator);
                tvExpression.setText(
                        formatResult(firstOperand) + " " + opSymbol
                                + " " + formatResult(secondOperand) + " ="
                );

                // Division by zero guard
                if (operator.equals("/") && secondOperand == 0) {
                    tvDisplay.setText("Error");
                    tvExpression.setText("Cannot divide by 0");
                    resetState();
                    return;
                }

                double result = calculate(firstOperand, secondOperand, operator);
                tvDisplay.setText(formatResult(result));
                currentInput  = formatResult(result);
                operator      = "";
                resultShown   = true;
                operatorPressed = false;
            }
        });

        // ----------------------------------------------------------------
        // Step 6: AC — All Clear, resets everything
        // ----------------------------------------------------------------
        btnAC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetState();
                tvDisplay.setText("0");
                tvExpression.setText("");
            }
        });

        // ----------------------------------------------------------------
        // Step 7: +/- Toggle sign of the current displayed number
        // ----------------------------------------------------------------
        btnToggleSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!currentInput.isEmpty() && !currentInput.equals("0")) {
                    if (currentInput.startsWith("-")) {
                        currentInput = currentInput.substring(1);
                    } else {
                        currentInput = "-" + currentInput;
                    }
                    tvDisplay.setText(currentInput);
                }
            }
        });

        // ----------------------------------------------------------------
        // Step 8: % — Convert current number to percentage (divide by 100)
        // ----------------------------------------------------------------
        btnPercent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!currentInput.isEmpty()) {
                    double val = parseInput() / 100.0;
                    currentInput = formatResult(val);
                    tvDisplay.setText(currentInput);
                }
            }
        });

        // ----------------------------------------------------------------
        // Step 9: Backspace ⌫ — remove last character from input
        // ----------------------------------------------------------------
        btnBackspace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!currentInput.isEmpty() && !resultShown) {
                    currentInput = currentInput.substring(0, currentInput.length() - 1);
                    tvDisplay.setText(currentInput.isEmpty() ? "0" : currentInput);
                } else if (resultShown) {
                    // If showing result, backspace clears it like AC
                    resetState();
                    tvDisplay.setText("0");
                    tvExpression.setText("");
                }
            }
        });

        // ----------------------------------------------------------------
        // Step 10: √ — Square root of current number
        //          Math.sqrt() returns double; display formatted result
        // ----------------------------------------------------------------
        btnSqrt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!currentInput.isEmpty()) {
                    double val = parseInput();
                    if (val < 0) {
                        Toast.makeText(MainActivity.this,
                                "Cannot take √ of negative number", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    double result = Math.sqrt(val);
                    tvExpression.setText("√(" + formatResult(val) + ")");
                    currentInput = formatResult(result);
                    tvDisplay.setText(currentInput);
                    resultShown = true;
                }
            }
        });

        // ----------------------------------------------------------------
        // Step 11: x² — Square the current number (Math.pow)
        // ----------------------------------------------------------------
        btnSquare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!currentInput.isEmpty()) {
                    double val    = parseInput();
                    double result = Math.pow(val, 2);
                    tvExpression.setText("(" + formatResult(val) + ")²");
                    currentInput = formatResult(result);
                    tvDisplay.setText(currentInput);
                    resultShown = true;
                }
            }
        });

        // ----------------------------------------------------------------
        // Step 12: 1/x — Reciprocal
        // ----------------------------------------------------------------
        btnReciprocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!currentInput.isEmpty()) {
                    double val = parseInput();
                    if (val == 0) {
                        Toast.makeText(MainActivity.this,
                                "Cannot divide by 0", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    double result = 1.0 / val;
                    tvExpression.setText("1/(" + formatResult(val) + ")");
                    currentInput = formatResult(result);
                    tvDisplay.setText(currentInput);
                    resultShown = true;
                }
            }
        });
    }

    // ====================================================================
    // HELPER METHODS
    // ====================================================================

    /**
     * calculate() — Core arithmetic logic
     * Performs the operation based on the stored operator string.
     * Uses Double.parseDouble() for parsing and standard Java operators.
     */
    private double calculate(double a, double b, String op) {
        switch (op) {
            case "+": return a + b;
            case "-": return a - b;
            case "*": return a * b;
            case "/": return a / b;
            default:  return b;
        }
    }

    /**
     * parseInput() — safely parses currentInput to double.
     * Returns 0 if the input is empty or malformed.
     */
    private double parseInput() {
        try {
            return Double.parseDouble(currentInput);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * formatResult() — formats a double for clean display.
     * If the result is a whole number (e.g. 6.0), shows "6" not "6.0".
     * If the result has decimal places, shows up to 8 significant digits.
     */
    private String formatResult(double result) {
        if (result == (long) result) {
            // Whole number — show without decimal point
            return String.valueOf((long) result);
        } else {
            // Decimal — limit to 8 characters max precision
            String s = String.valueOf(result);
            if (s.length() > 10) s = s.substring(0, 10);
            return s;
        }
    }

    /**
     * getOperatorSymbol() — converts internal operator char to display symbol
     */
    private String getOperatorSymbol(String op) {
        switch (op) {
            case "+": return "+";
            case "-": return "−";
            case "*": return "×";
            case "/": return "÷";
            default:  return op;
        }
    }

    /**
     * resetState() — resets all calculator state variables to initial values
     */
    private void resetState() {
        currentInput    = "";
        firstOperand    = 0;
        operator        = "";
        operatorPressed = false;
        resultShown     = false;
    }
}