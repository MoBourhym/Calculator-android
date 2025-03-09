package com.example.calculator;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.text.DecimalFormat;
import java.util.Random;

public class LandscapeActivity extends AppCompatActivity {
    private double firstNumber = 0.0;

    private double secondNumber = 0.0;
    private String currentOperation = null;
    private double result = 0.0;
    private boolean isNewOperation = true;
    private TextView display;
    private double currentValue = 0.0;
    private double memoryValue = 0.0;
    private boolean isNewInput = true;
    private String lastOperation = null;
    private boolean isInSecondMode = false;

    private final DecimalFormat formatter = new DecimalFormat("#,###.########");
    private final Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_landscape);

        display = findViewById(R.id.display);

        // Restore state if exists
        if (savedInstanceState != null) {
            restoreState(savedInstanceState);
        } else if (getIntent().getExtras() != null) {
            restoreState(getIntent().getExtras());
        }

        setupNumberButtons();
        setupScientificButtons();
        setupMemoryButtons();
        setupOperationButtons();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);


        Intent intent = new Intent(this, MainActivity.class);

        // Package current state into a bundle
        Bundle bundle = new Bundle();
        bundle.putDouble("firstNumber", firstNumber);
        bundle.putDouble("secondNumber", secondNumber);
        bundle.putString("currentOperation", currentOperation);
        bundle.putDouble("result", result);
        bundle.putBoolean("isNewOperation", isNewOperation);
        bundle.putString("displayText", display.getText().toString());

        // Attach the bundle to the intent
        intent.putExtras(bundle);

        // Start the new activity
        startActivity(intent);
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the current state
        outState.putDouble("firstNumber", firstNumber);
        outState.putDouble("secondNumber", secondNumber);
        outState.putString("currentOperation", currentOperation);
        outState.putDouble("result", result);
        outState.putBoolean("isNewOperation", isNewOperation);
        outState.putString("displayText", display.getText().toString());
    }

    private void restoreState(Bundle savedInstanceState) {
        // Restore the saved state
        firstNumber = savedInstanceState.getDouble("firstNumber", 0.0);
        secondNumber = savedInstanceState.getDouble("secondNumber", 0.0);
        currentOperation = savedInstanceState.getString("currentOperation");
        result = savedInstanceState.getDouble("result", 0.0);
        isNewOperation = savedInstanceState.getBoolean("isNewOperation", true);
        display.setText(savedInstanceState.getString("displayText", "0"));
    }


    private void setupNumberButtons() {
        int[] numberButtonIds = {
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3,
                R.id.btn4, R.id.btn5, R.id.btn6,
                R.id.btn7, R.id.btn8, R.id.btn9
        };

        for (int buttonId : numberButtonIds) {
            findViewById(buttonId).setOnClickListener(v -> {
                Button button = (Button) v;
                appendNumber(button.getText().toString());
            });
        }

        // Decimal point button
        findViewById(R.id.btnDecimal).setOnClickListener(v -> {
            if (!display.getText().toString().contains(".")) {
                appendNumber(".");
            }
        });
    }

    private void setupScientificButtons() {
        // Second mode toggle
        findViewById(R.id.btnSecond).setOnClickListener(v -> {
            isInSecondMode = !isInSecondMode;
            updateSecondModeButtons();
        });

        // Power and root functions
        findViewById(R.id.btnXSquared).setOnClickListener(v -> performUnaryOperation(x -> x * x));
        findViewById(R.id.btnXCubed).setOnClickListener(v -> performUnaryOperation(x -> x * x * x));
        findViewById(R.id.btnXPower).setOnClickListener(v -> triggerBinaryOperation("^"));
        findViewById(R.id.btn1DivX).setOnClickListener(v -> performUnaryOperation(x -> 1 / x));
        findViewById(R.id.btnSqrt).setOnClickListener(v -> performUnaryOperation(Math::sqrt));
        findViewById(R.id.btnCbrt).setOnClickListener(v -> performUnaryOperation(x -> Math.cbrt(x)));
        findViewById(R.id.btnYRootX).setOnClickListener(v -> triggerBinaryOperation("yroot"));

        // Exponential and logarithmic functions
        findViewById(R.id.btnExponent).setOnClickListener(v -> performUnaryOperation(Math::exp));
        findViewById(R.id.btn10Power).setOnClickListener(v -> performUnaryOperation(x -> Math.pow(10, x)));
        findViewById(R.id.btnLn).setOnClickListener(v -> performUnaryOperation(Math::log));
        findViewById(R.id.btnLog).setOnClickListener(v -> performUnaryOperation(Math::log10));

        // Trigonometric functions
        findViewById(R.id.btnSin).setOnClickListener(v -> performUnaryOperation(Math::sin));
        findViewById(R.id.btnCos).setOnClickListener(v -> performUnaryOperation(Math::cos));
        findViewById(R.id.btnTan).setOnClickListener(v -> performUnaryOperation(Math::tan));

        // Hyperbolic functions
        findViewById(R.id.btnSinh).setOnClickListener(v -> performUnaryOperation(Math::sinh));
        findViewById(R.id.btnCosh).setOnClickListener(v -> performUnaryOperation(Math::cosh));
        findViewById(R.id.btnTanh).setOnClickListener(v -> performUnaryOperation(Math::tanh));

        // Special constants and functions
        findViewById(R.id.btnE).setOnClickListener(v -> setDisplayValue(Math.E));
        findViewById(R.id.btnPi).setOnClickListener(v -> setDisplayValue(Math.PI));
        findViewById(R.id.btnRand).setOnClickListener(v -> setDisplayValue(random.nextDouble()));
        findViewById(R.id.btnX).setOnClickListener(v -> performUnaryOperation(this::factorial));
    }

    private void setupMemoryButtons() {
        findViewById(R.id.btnMC).setOnClickListener(v -> memoryValue = 0.0);
        findViewById(R.id.btnMPlus).setOnClickListener(v -> memoryValue += getCurrentValue());
        findViewById(R.id.btnMMinus).setOnClickListener(v -> memoryValue -= getCurrentValue());
        findViewById(R.id.btnMR).setOnClickListener(v -> setDisplayValue(memoryValue));
    }

    private void setupOperationButtons() {
        // Basic arithmetic operations
        findViewById(R.id.btnPlus).setOnClickListener(v -> triggerBinaryOperation("+"));
        findViewById(R.id.btnMinus).setOnClickListener(v -> triggerBinaryOperation("-"));
        findViewById(R.id.btnMultiply).setOnClickListener(v -> triggerBinaryOperation("×"));
        findViewById(R.id.btnDivide).setOnClickListener(v -> triggerBinaryOperation("÷"));

        // Additional utility buttons
        findViewById(R.id.btnEquals).setOnClickListener(v -> calculateResult());
        findViewById(R.id.btnAC).setOnClickListener(v -> clearAll());
        findViewById(R.id.btnPlusMinus).setOnClickListener(v -> toggleSign());
        findViewById(R.id.btnPercent).setOnClickListener(v -> calculatePercent());
        findViewById(R.id.btnDel).setOnClickListener(v -> deleteLastCharacter());
    }

    private void appendNumber(String number) {
        String currentText = display.getText().toString();
        if (isNewInput) {
            display.setText(number);
            isNewInput = false;
        } else {
            display.setText(currentText + number);
        }
    }

    private void setDisplayValue(double value) {
        display.setText(formatter.format(value));
        isNewInput = true;
    }

    private double getCurrentValue() {
        try {
            return Double.parseDouble(display.getText().toString().replace(",", "."));
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private void performUnaryOperation(UnaryOperator operator) {
        double value = getCurrentValue();
        setDisplayValue(operator.apply(value));
    }

    private void triggerBinaryOperation(String operation) {
        currentValue = getCurrentValue();
        lastOperation = operation;
        isNewInput = true;
    }

    private void calculateResult() {
        if (lastOperation == null) return;

        double secondValue = getCurrentValue();
        double result = 0.0;

        switch (lastOperation) {
            case "+": result = currentValue + secondValue; break;
            case "-": result = currentValue - secondValue; break;
            case "×": result = currentValue * secondValue; break;
            case "÷":
                if (secondValue != 0) {
                    result = currentValue / secondValue;
                } else {
                    display.setText("Error");
                    return;
                }
                break;
            case "^": result = Math.pow(currentValue, secondValue); break;
            case "yroot": result = Math.pow(currentValue, 1.0 / secondValue); break;
        }

        setDisplayValue(result);
        lastOperation = null;
    }

    private void clearAll() {
        display.setText("0");
        currentValue = 0.0;
        lastOperation = null;
        isNewInput = true;
    }

    private void toggleSign() {
        double value = getCurrentValue();
        setDisplayValue(-value);
    }

    private void calculatePercent() {
        double value = getCurrentValue();
        setDisplayValue(value / 100);
    }

    private void deleteLastCharacter() {
        String currentText = display.getText().toString();
        if (currentText.length() > 1) {
            display.setText(currentText.substring(0, currentText.length() - 1));
        } else {
            display.setText("0");
        }
    }

    private void updateSecondModeButtons() {
        // If you want to change button labels or functionality in second mode
        // Implement logic here
    }

    private double factorial(double n) {
        if (n < 0) return Double.NaN;
        if (n == 0 || n == 1) return 1;

        double result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }

    // Functional interface for unary operations
    private interface UnaryOperator {
        double apply(double x);
    }
}