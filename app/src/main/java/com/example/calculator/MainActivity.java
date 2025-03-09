package com.example.calculator;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {
    private TextView txtDisplay;
    private double firstNumber = 0.0;
    private double secondNumber = 0.0;
    private String currentOperation = null;
    private double result = 0.0;
    private boolean isNewOperation = true;
    private final DecimalFormat formatter = new DecimalFormat("#,###.########");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_landscape);

        // Handle window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtDisplay = findViewById(R.id.txtDisplay);
        setupNumberButtons();
        setupOperationButtons();

        // Check if we're recreating from a saved state or orientation change
        if (savedInstanceState != null) {
            restoreState(savedInstanceState);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Create an intent to switch to LandscapeActivity
        Intent intent = new Intent(this, LandscapeActivity.class);

        // Package current state into a bundle
        Bundle bundle = new Bundle();
        bundle.putDouble("firstNumber", firstNumber);
        bundle.putDouble("secondNumber", secondNumber);
        bundle.putString("currentOperation", currentOperation);
        bundle.putDouble("result", result);
        bundle.putBoolean("isNewOperation", isNewOperation);
        bundle.putString("displayText", txtDisplay.getText().toString());

        // Attach the bundle to the intent
        intent.putExtras(bundle);

        // Start the new activity
        startActivity(intent);
        finish(); // Close the current activity
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
        outState.putString("displayText", txtDisplay.getText().toString());
    }

    private void restoreState(Bundle savedInstanceState) {
        // Restore the saved state
        firstNumber = savedInstanceState.getDouble("firstNumber", 0.0);
        secondNumber = savedInstanceState.getDouble("secondNumber", 0.0);
        currentOperation = savedInstanceState.getString("currentOperation");
        result = savedInstanceState.getDouble("result", 0.0);
        isNewOperation = savedInstanceState.getBoolean("isNewOperation", true);
        txtDisplay.setText(savedInstanceState.getString("displayText", "0"));
    }

    private void setupNumberButtons() {
        int[] numberButtons = {
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9, R.id.btnComma
        };

        for (int buttonId : numberButtons) {
            findViewById(buttonId).setOnClickListener(this::buttonNumberClick);
        }
    }

    private void setupOperationButtons() {
        findViewById(R.id.btnAC).setOnClickListener(v -> clearAll());
        findViewById(R.id.btnPlusMinus).setOnClickListener(v -> changeSign());
        findViewById(R.id.btnPercent).setOnClickListener(v -> calculatePercent());

        int[] operationButtons = {R.id.btnDivide, R.id.btnMultiply, R.id.btnMinus, R.id.btnPlus};
        for (int buttonId : operationButtons) {
            findViewById(buttonId).setOnClickListener(this::buttonOperationClick);
        }
        findViewById(R.id.btnEquals).setOnClickListener(v -> calculateResult());
    }

    private void buttonNumberClick(View view) {
        Button button = (Button) view;
        String buttonText = button.getText().toString();

        if (isNewOperation) {
            txtDisplay.setText("");
            isNewOperation = false;
        }

        String currentDisplay = txtDisplay.getText().toString();

        if (buttonText.equals(",")) {
            if (!currentDisplay.contains(",")) {
                currentDisplay = currentDisplay.isEmpty() ? "0," : currentDisplay + ",";
            }
        } else {
            currentDisplay = currentDisplay.equals("0") ? buttonText : currentDisplay + buttonText;
        }

        txtDisplay.setText(currentDisplay);
    }

    private void buttonOperationClick(View view) {
        Button button = (Button) view;
        firstNumber = Double.parseDouble(txtDisplay.getText().toString().replace(",", "."));
        currentOperation = button.getText().toString();
        isNewOperation = true;
    }

    private void calculateResult() {
        if (currentOperation == null) return;

        String displayText = txtDisplay.getText().toString();
        if (!displayText.isEmpty()) {
            secondNumber = Double.parseDouble(displayText.replace(",", "."));
            switch (currentOperation) {
                case "+": result = firstNumber + secondNumber; break;
                case "-": result = firstNumber - secondNumber; break;
                case "x": result = firstNumber * secondNumber; break;
                case "/":
                    if (secondNumber != 0.0) {
                        result = firstNumber / secondNumber;
                    } else {
                        txtDisplay.setText("Error");
                        return;
                    }
                    break;
            }
            txtDisplay.setText(formatter.format(result).replace(".", ","));
            firstNumber = result;
            isNewOperation = true;
        }
    }

    private void clearAll() {
        txtDisplay.setText("0");
        firstNumber = 0.0;
        secondNumber = 0.0;
        currentOperation = null;
        isNewOperation = true;
    }

    private void changeSign() {
        String currentValue = txtDisplay.getText().toString();
        if (!currentValue.equals("0")) {
            txtDisplay.setText(currentValue.startsWith("-") ? currentValue.substring(1) : "-" + currentValue);
        }
    }

    private void calculatePercent() {
        double currentValue = Double.parseDouble(txtDisplay.getText().toString().replace(",", "."));
        txtDisplay.setText(formatter.format(currentValue / 100).replace(".", ","));
        isNewOperation = true;
    }
}
