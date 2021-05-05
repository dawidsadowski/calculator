package com.example.calculator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
    }

    public void simpleButtonAction(View v) {
        Intent intent = new Intent(getBaseContext(), CalculatorModesActivity.class);
        intent.putExtra("mode", Mode.SIMPLE);
        startActivity(intent);
    }

    public void advancedButtonAction(View v) {
        Intent intent = new Intent(getBaseContext(), CalculatorModesActivity.class);
        intent.putExtra("mode", Mode.ADVANCED);
        startActivity(intent);
    }

    public void aboutButtonAction(View v) {
        startActivity(new Intent(getBaseContext(), AboutActivity.class));
    }

    public void exitApplication(View v) {
        finish();
        System.exit(0);
    }
}