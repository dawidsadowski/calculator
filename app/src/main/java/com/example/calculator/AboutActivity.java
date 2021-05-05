package com.example.calculator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        getSupportActionBar().hide();

        TextView appIconAuthorTextView = (TextView) findViewById(R.id.appIconAuthorTextView);
        appIconAuthorTextView.setMovementMethod(LinkMovementMethod.getInstance());
    }
}