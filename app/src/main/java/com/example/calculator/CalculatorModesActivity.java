package com.example.calculator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.DecimalFormat;
import java.util.Locale;

public class CalculatorModesActivity extends AppCompatActivity {
    private final DecimalFormat decimalFormat = new DecimalFormat("0.#######");
    private final DecimalFormat scientificFormat = new DecimalFormat("0.00#####E0");
    private Toast toast = null;

    private double number1 = 0;
    private double number2 = 0;
    private double result = 0;
    private int stage = 0;
    private int operation = 0;
    private boolean dotPlaced = false;
    private TextView screenTextView;

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putDouble("number1", number1);
        outState.putDouble("number2", number2);
        outState.putDouble("result", result);
        outState.putInt("stage", stage);
        outState.putInt("operation", operation);
        outState.putBoolean("dotPlaced", dotPlaced);

        if(screenTextView != null) {
            outState.putString("screenText", screenTextView.getText().toString());
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        number1 = savedInstanceState.getDouble("number1");
        number2 = savedInstanceState.getDouble("number2");
        result = savedInstanceState.getDouble("result");
        stage = savedInstanceState.getInt("stage");
        operation = savedInstanceState.getInt("operation");
        dotPlaced = savedInstanceState.getBoolean("dotPlaced");

        screenTextView = findViewById(R.id.screenTextView);
        String displayNumber = savedInstanceState.getString("screenText");

        if(screenTextView != null) {
            if(displayNumber != null) {
                screenTextView.setText(displayNumber);
            } else {
                screenTextView.setText("0");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();

        setContentView(R.layout.activity_calculator_modes);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        Mode mode = (Mode) getIntent().getSerializableExtra("mode");
        Fragment fragment = new Fragment();

        switch (mode) {
            case SIMPLE:
                fragment = new SimpleFragment();
                bottomNavigationView.setSelectedItemId(R.id.nav_simple);
                break;

            case ADVANCED:
                fragment = new AdvancedFragment();
                bottomNavigationView.setSelectedItemId(R.id.nav_advanced);
                break;
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                fragment).commit();
    }

    public void showToast(CharSequence text) {
        if(toast != null) toast.cancel();

        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public void digitPressed(View v) {
        Button button = (Button) v;
        String numberText = button.getText().toString();

        Activity activity = (Activity) button.getContext();
        screenTextView = activity.findViewById(R.id.screenTextView);

        switch (stage) {
            case 0:
            case 2:
                if (screenTextView.getText().toString().compareTo("0") == 0) {
                    screenTextView.setText(numberText);
                } else {
                    screenTextView.append(numberText);
                }
                break;

            case 1:
                if(dotPlaced) {
                    screenTextView.append(numberText);
                } else {
                    screenTextView.setText(numberText);
                }

                stage++;
                break;

            case 3:
                if(!dotPlaced) {
                    number1 = number2 = result = stage = 0;
                    screenTextView.setText("");
                    digitPressed(v);
                }
        }
    }

    public void doubleOperatorHandler(View v) {
        dotPlaced = false;

        Activity activity = (Activity) v.getContext();
        screenTextView = activity.findViewById(R.id.screenTextView);

        if(stage == 0) {
            number1 = Double.parseDouble(screenTextView.getText().toString());
            stage++;
        }

        if(stage == 2) {
            number2 = Double.parseDouble(screenTextView.getText().toString());
        }

        if(stage == 2) {
            performDoubleOperation(v);
        }

        number2 = number1;
        operation = v.getId();
        stage = 1;
    }

    // IntelliJ suggestion
    @SuppressLint("NonConstantResourceId")
    public void performDoubleOperation(View v) {
        dotPlaced = false;

        Button button = (Button) v;
        Activity activity = (Activity) button.getContext();
        screenTextView = activity.findViewById(R.id.screenTextView);

        if(stage == 2) {
            number2 = Double.parseDouble(screenTextView.getText().toString());
        }

        if(v.getId() == R.id.percentButton) {
            number2 = number2 * number1 / 100;
        }

        switch (operation) {
            case R.id.additionButton:
                result = number1 + number2;
                break;

            case R.id.subtractionButton:
                result = number1 - number2;
                break;

            case R.id.multiplicationButton:
                result = number1 * number2;
                break;

            case R.id.divisionButton:
                if(number2 == 0) {
                    showToast(getString(R.string.error_zero_division));
                    return;
                }

                result = number1 / number2;
                break;

            case R.id.xpoweryButton:
                result = Math.pow(number1, number2);
                break;

            default:
                result = Double.parseDouble(screenTextView.getText().toString());
        }

        number1 = result;
        stage = 3;
        screenTextView.setText(formatNumber(result));
    }

    public String formatNumber(double number) {
        if(number == Double.POSITIVE_INFINITY || number == Double.NEGATIVE_INFINITY) {
            showToast(getString(R.string.limit_reached));
            number = 0;
        }

        Double d = (Double) number;

        if(number == (long) number && d.toString().length() < 13) {
            return String.format(Locale.US,"%d", (long) number);
        }

        String[] parts = d.toString().split("\\.");

        String formattedNumber = decimalFormat.format(number);

        if(formattedNumber.length() > 11) {
            formattedNumber = scientificFormat.format(number);
        }

        return formattedNumber;
    }

    @SuppressLint("NonConstantResourceId")
    public double performSingleOperation(double number, int operation) throws Exception {
        switch (operation) {
            case R.id.plusMinusButton:
                return -number;

            case R.id.sinusButton:
                return Math.sin(number);

            case R.id.cosinusButton:
                return Math.cos(number);

            case R.id.tangensButton:
                return Math.tan(number);

            // TODO: May be logarithm_x of number. If so move it to double operator method
            case R.id.logarithmButton:
                if(number <= 0) {
                    throw new Exception(getString(R.string.incorrect_number_passed));
                }

                return Math.log10(number);

            case R.id.logarithmNButton:
                if(number <= 0) {
                    throw new Exception(getString(R.string.incorrect_number_passed));
                }

                return Math.log(number);

            case R.id.sqrtButton:
                if(number < 0) {
                    throw new Exception(getString(R.string.incorrect_number_passed));
                }

                return Math.sqrt(number);

            case R.id.xpower2Button:
                return number * number;
        }

        return number;
    }

    public void singleArgumentHandler(View v) {
        Activity activity = (Activity) v.getContext();
        screenTextView = activity.findViewById(R.id.screenTextView);

        double inputNumber = Double.parseDouble(screenTextView.getText().toString());
        double number;

        try {
            number = performSingleOperation(inputNumber, v.getId());
        } catch(Exception e) {
            showToast(e.getMessage());
            return;
        }


        if(stage == 2) {
            number2 = number;
        }

        if(stage == 3) {
            result = number1 = number;
        }

        if(stage == 1) {
            number2 = number;
            stage = 2;
        }

        screenTextView.setText(formatNumber(number));
    }

    public void clear(View v) {
        dotPlaced = false;

        Activity activity = (Activity) v.getContext();
        screenTextView = activity.findViewById(R.id.screenTextView);

        int button = v.getId();

        if(screenTextView.getText().toString().compareTo("0") == 0 || button == R.id.acButton) {
            number1 = number2 = result = stage = 0;
        }

        screenTextView.setText("0");

        if(stage == 3) {
            number1 = 0;
        }

        if(stage == 2) {
            number2 = 0;
        }

        if(stage == 1) {
            number2 = 0;
            stage = 2;
        }
    }

    public void insertDot(View v) {
        Activity activity = (Activity) v.getContext();
        screenTextView = activity.findViewById(R.id.screenTextView);

        if(!dotPlaced) {
            dotPlaced = true;

            if(stage == 1 || stage == 3) {
                screenTextView.setText("0.");
            } else {
                screenTextView.append(".");
            }
        }
    }

    @SuppressLint("NonConstantResourceId") // Android Studio suggestion
    private final BottomNavigationView.OnNavigationItemSelectedListener navListener =
            (MenuItem item) -> {
                Fragment fragment = null;

                switch (item.getItemId()) {
                    case R.id.nav_simple:
                        fragment = new SimpleFragment();
                        getIntent().putExtra("mode", Mode.SIMPLE);
                        break;

                    case R.id.nav_advanced:
                        fragment = new AdvancedFragment();
                        getIntent().putExtra("mode", Mode.ADVANCED);
                        break;
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        fragment).commit();

                return true;
            };
}